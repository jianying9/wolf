package com.wolf.framework.websocket;

import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.config.ResponseCodeConfig;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.context.Resource;
import com.wolf.framework.logger.AccessLogger;
import com.wolf.framework.logger.AccessLoggerFactory;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.worker.ServiceWorker;
import com.wolf.framework.worker.context.WebSocketWorkerContextImpl;
import java.io.IOException;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author jianying9
 */
@ServerEndpoint(
        value = "/ws/api/{text}",
        configurator = WebsocketEndPoint.WebsocketServerConfigurator.class
)
public class WebsocketEndPoint implements Resource {

    private final SessionManager sessionManager;
    private final Logger logger = LogFactory.getLogger(FrameworkLogger.FRAMEWORK);
    private final Pattern routePattern = Pattern.compile("(?:\"route\":\")([a-zA-Z/\\d]+)(?:\")");
    private final long expireTime;

    public WebsocketEndPoint() {
        this.logger.info("WebsocketServer:WebsocketEndPoint start.....");
        this.sessionManager = new SessionManager();
        this.expireTime = 1000 * 120;
        //注册推送服务
        ApplicationContext.CONTEXT.getPushContext().setPushHandler(this.sessionManager);
    }

    public SessionManager getSessionManager() {
        return this.sessionManager;
    }

    private void exec(String text, Session session, boolean isAsync) {
        long start = System.currentTimeMillis();
        Matcher matcher = this.routePattern.matcher(text);
        String responseMesssage;
        String sid = "";
        String route;
        if (matcher.find()) {
            route = matcher.group(1);
            ServiceWorker serviceWorker = ApplicationContext.CONTEXT.getServiceWorker(route);
            if (serviceWorker == null) {
                //无效的route
                responseMesssage = "{\"code\":\"" + ResponseCodeConfig.NOTFOUND + "\",\"route\":\"" + route + "\"}";
            } else {
                //创建消息对象并执行服务
                WebSocketWorkerContextImpl workerContext = new WebSocketWorkerContextImpl(this.getSessionManager(), session, route, serviceWorker, "");
                workerContext.initWebsocketParameter(text);
                serviceWorker.doWork(workerContext);
                //返回消息
                responseMesssage = workerContext.getWorkerResponse().getResponseMessage();
                sid = workerContext.getSessionId();
                //
                if (serviceWorker.getServiceContext().isSaveLog()) {
                    long time = System.currentTimeMillis() - start;
                    AccessLogger accessLogger = AccessLoggerFactory.getAccessLogger();
                    accessLogger.log(route, sid, text, responseMesssage, time);
                    String code = workerContext.getWorkerResponse().getCode();
                    if (code.equals(ResponseCodeConfig.SUCCESS)) {
                        accessLogger.log(route, sid, text, responseMesssage, time);
                    } else {
                        accessLogger.error(route, sid, text, responseMesssage, time);
                    }
                }
            }
        } else {
            responseMesssage = "{\"code\":\"" + ResponseCodeConfig.INVALID + "\",\"error\":\"route is null\"}";
        }
        if (responseMesssage.isEmpty() == false) {
            if (isAsync) {
                session.getAsyncRemote().sendText(responseMesssage);
            } else {
                try {
                    session.getBasicRemote().sendText(responseMesssage);
                } catch (IOException ex) {
                }
            }
        }
        //
        Object s = session.getUserProperties().get(WebsocketConfig.SID_NAME);
        Object l = session.getUserProperties().get(WebsocketConfig.LAST_TIME_NAME);
        if (s == null || l == null) {
            try {
                session.close();
            } catch (IOException ex) {
            }
        } else {
            long lastTime = (Long) l;
            if (System.currentTimeMillis() - lastTime > this.expireTime) {
                //心跳超时，关闭接口
                try {
                    session.getBasicRemote().sendText("{\"code\":\"" + ResponseCodeConfig.TIMEOUT + "\"}");
                    session.close();
                    AccessLogger accessLogger = AccessLoggerFactory.getAccessLogger();
                    accessLogger.log(sid, "wobsocket", ResponseCodeConfig.TIMEOUT);
                } catch (IOException ex) {
                }
            }
        }
    }

    @OnOpen
    public void onOpen(@PathParam("text") String text, Session session) {
        //解密
        char[] charArray = text.toCharArray();
        try {
            byte[] byteArray = Hex.decodeHex(charArray);
            text = new String(byteArray);
        } catch (DecoderException ex) {
        }
        //记录首次时间
        session.getUserProperties().put(WebsocketConfig.LAST_TIME_NAME, System.currentTimeMillis());
        this.exec(text, session, false);
        //
        String sid = "no sid";
        Object s = session.getUserProperties().get(WebsocketConfig.SID_NAME);
        if (s != null) {
            sid = (String) s;
        }
        AccessLogger accessLogger = AccessLoggerFactory.getAccessLogger();
        accessLogger.log(sid, "wobsocket", "onOpen");
    }

    @OnMessage
    public void onMessage(String text, Session session) {
        switch (text) {
            case WebsocketConfig.PING_TEXT:
                this.onPing(session);
                break;
            case WebsocketConfig.PONG_TEXT:
                this.onPong(session);
                break;
            default:
                this.exec(text, session, true);
                break;
        }
    }

    private void onPing(Session session) {
        //记录最后的心跳时间
        session.getUserProperties().put(WebsocketConfig.LAST_TIME_NAME, System.currentTimeMillis());
        session.getAsyncRemote().sendText(WebsocketConfig.PONG_TEXT);
    }

    private void onPong(Session session) {
        //记录最后的心跳时间
        session.getUserProperties().put(WebsocketConfig.LAST_TIME_NAME, System.currentTimeMillis());
    }

    @OnClose
    public void onClose(Session session) {
        String sid = "no sid";
        Object s = session.getUserProperties().get(WebsocketConfig.SID_NAME);
        if (s != null) {
            sid = (String) s;
            this.sessionManager.remove(sid);
        }
        AccessLogger accessLogger = AccessLoggerFactory.getAccessLogger();
        accessLogger.log(sid, "wobsocket", "onClose");
    }

    @OnError
    public void onError(Throwable t, Session session) throws IOException {
        String sid = "no sid";
        Object s = session.getUserProperties().get(WebsocketConfig.SID_NAME);
        if (s != null) {
            sid = (String) s;
        }
        session.close();
        //
        AccessLogger accessLogger = AccessLoggerFactory.getAccessLogger();
        accessLogger.log(sid, "wobsocket", "onError");
    }

    @Override
    public void destory() {
        this.logger.info("wobsocket-shutdown.....");
        Collection<Session> sessions = this.sessionManager.getSessions();
        try {
            for (Session session : sessions) {
                session.close();
            }
        } catch (IOException ex) {
        }
    }

    public static class WebsocketServerConfigurator extends ServerEndpointConfig.Configurator {

        public static final WebsocketEndPoint END_POINT = new WebsocketEndPoint();

        static {
            ApplicationContext.CONTEXT.addResource(END_POINT);
        }

        @Override
        public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
            if (endpointClass.equals(WebsocketEndPoint.class)) {
                return (T) END_POINT;
            } else {
                return super.getEndpointInstance(endpointClass);
            }
        }
    }
}
