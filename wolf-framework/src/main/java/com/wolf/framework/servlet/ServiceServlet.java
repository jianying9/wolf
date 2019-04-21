package com.wolf.framework.servlet;

import com.wolf.framework.push.CometHandler;
import com.wolf.framework.config.FrameworkConfig;
import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.config.ResponseCodeConfig;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.logger.AccessLogger;
import com.wolf.framework.logger.AccessLoggerFactory;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.utils.HttpUtils;
import com.wolf.framework.utils.StringUtils;
import com.wolf.framework.worker.ServiceWorker;
import com.wolf.framework.worker.context.ServletWorkerContextImpl;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;

/**
 *
 * @author jianying9
 */
@WebServlet(name = "server", loadOnStartup = 1, urlPatterns = {"/http/api/*"}, asyncSupported = true)
public class ServiceServlet extends HttpServlet implements CometHandler {

    private final Logger logger = LogFactory.getLogger(FrameworkLogger.FRAMEWORK);
    private final Map<String, AsyncContext> asyncContextMap = new HashMap(32, 1);
    private final Map<String, MessageCache> messageCacheMap = new HashMap(32, 1);
    private final AsyncListener asyncListener = new AsyncPushListener();
    private long asyncTimeOut = 60000;
    private long lastCheckTime = 0;
    private String referer = "";
    private boolean canComet = false;

    @Override
    public void init() throws ServletException {
        this.logger.info("ServerServlet start.....");
        String asyncPushTimeout = ApplicationContext.CONTEXT.getParameter(FrameworkConfig.ASYNC_PUSH_TIMEOUT);
        if (asyncPushTimeout != null) {
            try {
                this.asyncTimeOut = Long.parseLong(asyncPushTimeout);
            } catch (NumberFormatException e) {
            }
        }
        String httpReferer = ApplicationContext.CONTEXT.getParameter(FrameworkConfig.HTTP_REFERER);
        if (httpReferer != null) {
            this.referer = httpReferer;
        }
        //注册推送服务
        String comet = ApplicationContext.CONTEXT.getParameter(FrameworkConfig.HTTP_COMET);
        if (comet != null) {
            this.canComet = Boolean.valueOf(comet);
        }
        if (this.canComet) {
            ApplicationContext.CONTEXT.getPushContext().setCometHandler(this);
        }
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        boolean validReferer = false;
        if (this.referer != null && this.referer.isEmpty() == false) {
            String r = request.getHeader("Referer");
            if (r != null && r.contains(this.referer)) {
                validReferer = true;
            }
        } else {
            validReferer = true;
        }
        if (validReferer) {
            //读取参数
            Map<String, String> parameterMap;
            String route = request.getPathInfo();
            Enumeration<String> names = request.getParameterNames();
            parameterMap = new HashMap(8, 1);
            String name;
            String value;
            while (names.hasMoreElements()) {
                name = names.nextElement();
                value = request.getParameter(name);
                value = StringUtils.trim(value);
                parameterMap.put(name, value);
            }
            ServiceWorker serviceWorker = ApplicationContext.CONTEXT.getServiceWorker(route);
            if (serviceWorker == null) {
                //route不存在,判断是否为comet请求
                String comet = parameterMap.get("comet");
                if (comet != null && comet.equals("start") && this.canComet) {
                    //该请求为一个长轮询推送请求
                    String sid = parameterMap.get("sid");
                    if (sid != null) {
                        //判断缓存中是否有消息
                        String msgCache = null;
                        MessageCache messageCache = this.messageCacheMap.get(sid);
                        if (messageCache != null) {
                            msgCache = messageCache.poll();
                        }
                        //
                        if (msgCache != null) {
                            //有缓存的消息,直接返回
                            HttpUtils.toWrite(request, response, msgCache);
                        } else {
                            //没有缓存消息,当前请求加入长轮询
                            synchronized (this) {
                                //同sid冲突检测
                                AsyncContext ctx = this.asyncContextMap.get(sid);
                                if (ctx != null) {
                                    String stopMessage = "{\"comet\":\"stop\"}";
                                    HttpUtils.toWrite(ctx.getRequest(), ctx.getResponse(), stopMessage);
                                    ctx.complete();
                                    this.asyncContextMap.remove(sid);
                                }
                                ctx = request.startAsync(request, response);
                                ctx.setTimeout(this.asyncTimeOut);
                                ctx.addListener(this.asyncListener);
                                this.asyncContextMap.put(sid, ctx);
                            }
                        }
                    } else {
                        //无效的comet
                        String result = "{\"comet\":\"invalid\",\"error\":\"push sid not exist\"}";
                        HttpUtils.toWrite(request, response, result);
                    }
                } else {
                    //非特殊接口,放回提示route不存在
                    String result = "{\"code\":\"" + ResponseCodeConfig.NOTFOUND + "\",\"route\":\"" + route + "\"}";
                    HttpUtils.toWrite(request, response, result);
                }
            } else {
                //route存在
                long start = System.currentTimeMillis();
                String sid = parameterMap.get("sid");
                ServletWorkerContextImpl workerContext = new ServletWorkerContextImpl(this, sid, route, serviceWorker);
                String param = parameterMap.get("_json");
                workerContext.initHttpParameter(parameterMap, param);
                serviceWorker.doWork(workerContext);
                String result = workerContext.getWorkerResponse().getResponseMessage();
                HttpUtils.toWrite(request, response, result);
                //
                if (param == null || param.isEmpty()) {
                    ObjectMapper mapper = new ObjectMapper();
                    param = mapper.writeValueAsString(parameterMap);
                }
                if (serviceWorker.getServiceContext().isSaveLog()) {
                    long time = System.currentTimeMillis() - start;
                    AccessLogger accessLogger = AccessLoggerFactory.getAccessLogger();
                    accessLogger.log(route, sid, param, result, time);
                }
            }
        }
        //每个5分钟触发检查缓存消息过时的
        long currentTime = System.currentTimeMillis();
        if ((currentTime - this.lastCheckTime) >= 300000) {
            this.lastCheckTime = currentTime;
            this.checkMessageCache(currentTime);
        }
    }

    private synchronized void checkMessageCache(long currentTime) {
        Set<Entry<String, MessageCache>> entrySet = this.messageCacheMap.entrySet();
        MessageCache messageCache;
        long lastUpdateTime;
        Set<String> expireSet = new HashSet();
        for (Entry<String, MessageCache> entry : entrySet) {
            messageCache = entry.getValue();
            lastUpdateTime = messageCache.getLastUpdateTime();
            if ((currentTime - lastUpdateTime) > 200000) {
                expireSet.add(entry.getKey());
            }
        }
        //
        for (String sid : expireSet) {
            this.messageCacheMap.remove(sid);
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "server";
    }

    @Override
    public boolean asyncPush(String sid, String route, String message) {
        return this.push(sid, route, message);
    }

    @Override
    public boolean push(String sid, String route, String message) {
        boolean result = false;
        //同sid冲突检测
        //
        AccessLogger accessLogger = AccessLoggerFactory.getAccessLogger();
        AsyncContext ctx = this.asyncContextMap.get(sid);
        if (ctx != null) {
            result = true;
            HttpUtils.toWrite(ctx.getRequest(), ctx.getResponse(), message);
            ctx.complete();
            this.asyncContextMap.remove(sid);
            accessLogger.log(route, sid, "", message, -1);
        } else {
            synchronized (this) {
                MessageCache messageCache = this.messageCacheMap.get(sid);
                if (messageCache == null) {
                    messageCache = new MessageCache();
                    this.messageCacheMap.put(sid, messageCache);
                }
                messageCache.offer(message);
            }
            accessLogger.log(sid, "http", "cache");
        }
        return result;
    }

    @Override
    public String toString() {
        return "async-servlet:".concat(super.toString());
    }

    /**
     * AsyncContext事件监听
     */
    private class AsyncPushListener implements AsyncListener {

        @Override
        public void onComplete(AsyncEvent event) throws IOException {
        }

        @Override
        public void onTimeout(AsyncEvent event) throws IOException {
            AsyncContext ctx = event.getAsyncContext();
            String sid = ctx.getRequest().getParameter("sid");
            asyncContextMap.remove(sid);
            String continueMessage = "{\"comet\":\"timeout\"}";
            HttpUtils.toWrite(ctx.getRequest(), ctx.getResponse(), continueMessage);
            ctx.complete();
        }

        @Override
        public void onError(AsyncEvent event) throws IOException {
        }

        @Override
        public void onStartAsync(AsyncEvent event) throws IOException {
        }
    }

    public void saveNewSession(String sid) {
        AccessLogger accessLogger = AccessLoggerFactory.getAccessLogger();
        accessLogger.log(sid, "http", "add");
    }

    public void removeSession(String sid) {
        AsyncContext ctx = this.asyncContextMap.get(sid);
        if (ctx != null) {
            String stopMessage = "{\"comet\":\"stop\"}";
            HttpUtils.toWrite(ctx.getRequest(), ctx.getResponse(), stopMessage);
            ctx.complete();
            this.asyncContextMap.remove(sid);
        }
        AccessLogger accessLogger = AccessLoggerFactory.getAccessLogger();
        accessLogger.log(sid, "http", "remove");
    }
}
