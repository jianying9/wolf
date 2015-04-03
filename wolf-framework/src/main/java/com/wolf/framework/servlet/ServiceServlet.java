package com.wolf.framework.servlet;

import com.wolf.framework.comet.CometHandler;
import com.wolf.framework.config.FrameworkConfig;
import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.utils.HttpUtils;
import com.wolf.framework.utils.StringUtils;
import com.wolf.framework.worker.ServiceWorker;
import com.wolf.framework.worker.context.ServletWorkerContextImpl;
import com.wolf.framework.worker.context.WorkerContext;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;

/**
 *
 * @author aladdin
 */
@WebServlet(name = "server", loadOnStartup = 1, urlPatterns = {"/*"}, asyncSupported = true)
public class ServiceServlet extends HttpServlet implements CometHandler {

    private static final long serialVersionUID = -2251705966222970110L;
    private final Logger logger = LogFactory.getLogger(FrameworkLogger.FRAMEWORK);
    Map<String, AsyncContext> asyncContextMap = new HashMap<String, AsyncContext>(32, 1);
    private final AsyncListener asyncListener = new AsyncPushListener();
    private long asyncTimeOut = 60000;

    @Override
    public void init() throws ServletException {
        String asyncPushTimeout = ApplicationContext.CONTEXT.getParameter(FrameworkConfig.ASYNC_PUSH_TIMEOUT);
        if (asyncPushTimeout != null) {
            try {
                this.asyncTimeOut = Long.parseLong(asyncPushTimeout);
            } catch (NumberFormatException e) {
            }
        }
        //注册推送服务
        ApplicationContext.CONTEXT.getCometContext().addCometHandler(this);
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
        String result;
        //读取参数
        Enumeration<String> names = request.getParameterNames();
        Map<String, String> parameterMap = new HashMap<String, String>(8, 1);
        String name;
        String value;
        while (names.hasMoreElements()) {
            name = names.nextElement();
            value = request.getParameter(name);
            value = StringUtils.trim(value);
            parameterMap.put(name, value);
        }
        this.logger.debug("http: {}", parameterMap);
        //
        String route = request.getPathInfo();
        ServiceWorker serviceWorker = ApplicationContext.CONTEXT.getServiceWorker(route);
        if (serviceWorker == null) {
            String wolf = parameterMap.get("wolf");
            if (wolf != null) {
                if (wolf.equals("TIME")) {
                    //获取系统时间
                    long time = System.currentTimeMillis();
                    StringBuilder resultBuilder = new StringBuilder(25);
                    resultBuilder.append("{\"wolf\":\"TIME\",\"time\":").append(Long.toString(time)).append('}');
                    result = resultBuilder.toString();
                    HttpUtils.toWrite(request, response, result);
                } else if (wolf.equals("PUSH")) {
                    //该请求为一个长轮询推送请求
                    String sid = parameterMap.get("sid");
                    if (sid != null) {
                        synchronized (this) {
                            //同sid冲突检测
                            AsyncContext ctx = this.asyncContextMap.get(sid);
                            if (ctx != null) {
                                String stopMessage = "{\"wolf\":\"PUSH_STOP\"}";
                                HttpUtils.toWrite(ctx.getRequest(), ctx.getResponse(), stopMessage);
                                ctx.complete();
                                this.asyncContextMap.remove(sid);
                            }
                            ctx = request.startAsync(request, response);
                            ctx.setTimeout(this.asyncTimeOut);
                            ctx.addListener(this.asyncListener);
                            this.asyncContextMap.put(sid, ctx);
                        }
                    } else {
                        //无效的wolf
                        result = "{\"wolf\":\"INVALID\",\"error\":\"push sid not exist\"}";
                        HttpUtils.toWrite(request, response, result);
                    }
                } else {
                    //无效的wolf
                    result = "{\"wolf\":\"INVALID\",\"error\":\"wolf not exist\"}";
                    HttpUtils.toWrite(request, response, result);
                }
            } else {
                //无效的act
                result = "{\"state\":\"INVALID\",\"error\":\"route[" + route + "] not exist\"}";
                HttpUtils.toWrite(request, response, result);
            }
        } else {
            String sid = parameterMap.get("sid");
            WorkerContext workerContext = new ServletWorkerContextImpl(this, sid, route, parameterMap);
            serviceWorker.doWork(workerContext);
            result = serviceWorker.getResponse().getResponseMessage();
            HttpUtils.toWrite(request, response, result);
        }
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
    public boolean push(String sid, String message) {
        this.logger.debug("async-servlet push message:{},{}", sid, message);
        boolean result = false;
        //同sid冲突检测
        AsyncContext ctx = this.asyncContextMap.get(sid);
        if (ctx != null) {
            result = true;
            HttpUtils.toWrite(ctx.getRequest(), ctx.getResponse(), message);
            ctx.complete();
            this.asyncContextMap.remove(sid);
        } else {
            this.logger.debug("async-servlet push message:sid not exist:{}", sid);
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
            String continueMessage = "{\"wolf\":\"PUSH_TIMEOUT\"}";
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
        this.logger.debug("async-servlet add session:{}", sid);
    }

    public void removeSession(String sid) {
        this.logger.debug("async-servlet remove session:{}", sid);
        AsyncContext ctx = this.asyncContextMap.get(sid);
        if (ctx != null) {
            String stopMessage = "{\"wolf\":\"PUSH_STOP\"}";
            HttpUtils.toWrite(ctx.getRequest(), ctx.getResponse(), stopMessage);
            ctx.complete();
            this.asyncContextMap.remove(sid);
        }
    }
}
