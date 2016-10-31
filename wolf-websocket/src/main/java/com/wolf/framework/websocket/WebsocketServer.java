package com.wolf.framework.websocket;

import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.config.ResponseCodeConfig;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.worker.ServiceWorker;
import com.wolf.framework.worker.context.WebSocketWorkerContextImpl;
import com.wolf.framework.worker.context.WorkerContext;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import org.slf4j.Logger;

/**
 *
 * @author jianying9
 */
@ServerEndpoint(value = "/api/{text}")
public class WebsocketServer {

    private final SessionManager sessionManager;
    private final Logger logger = LogFactory.getLogger(FrameworkLogger.WEBSOCKET);
    private final Pattern routePattern = Pattern.compile("(?:\"route\":\")([a-zA-Z/\\d]+)(?:\")");
    private final long expireTime = 1000 * 60;

    public WebsocketServer() {
        this.sessionManager = new SessionManager();
        //注册推送服务
        ApplicationContext.CONTEXT.getCometContext().addCometHandler(this.sessionManager);
    }

    public SessionManager getSessionManager() {
        return this.sessionManager;
    }

    private void exec(String text, Session session, boolean isAsync) {
        this.logger.debug("wobsocket-on message:{}", text);
        Matcher matcher = this.routePattern.matcher(text);
        String responseMesssage;
        if (matcher.find()) {
            String route = matcher.group(1);
            ServiceWorker serviceWorker = ApplicationContext.CONTEXT.getServiceWorker(route);
            if (serviceWorker == null) {
                //无效的route
                responseMesssage = "{\"code\":\"" + ResponseCodeConfig.NOTFOUND + "\",\"route\":\"" + route + "\"}";
            } else {
                //创建消息对象并执行服务
                WorkerContext workerContext = new WebSocketWorkerContextImpl(this.getSessionManager(), session, route, text, serviceWorker);
                serviceWorker.doWork(workerContext);
                //返回消息
                responseMesssage = workerContext.getWorkerResponse().getResponseMessage();
            }
        } else {
            responseMesssage = "{\"code\":\"" + ResponseCodeConfig.INVALID + "\",\"error\":\"route is null\"}";
        }
        if(isAsync) {
            session.getAsyncRemote().sendText(responseMesssage);
        } else {
            try {
                session.getBasicRemote().sendText(responseMesssage);
            } catch (IOException ex) {
            }
        }
        this.logger.debug("wobsocket-send message:{}", responseMesssage);
        Object s = session.getUserProperties().get(WebsocketConfig.SID_NAME);
        Object l = session.getUserProperties().get(WebsocketConfig.LAST_TIME_NAME);
        if (s == null || l == null) {
            try {
                session.close();
            } catch (IOException ex) {
            }
        }
        long lastTime = (Long) l;
        if(System.currentTimeMillis() - lastTime > this.expireTime) {
            //心跳超时，关闭接口
            try {
                session.getBasicRemote().sendText("{\"code\":\"" + ResponseCodeConfig.TIMEOUT + "\"}");
                session.close();
            } catch (IOException ex) {
            }
        }
    }

    @OnOpen
    public void onOpen(@PathParam("text") String text, Session session) {
        //记录首次时间
        session.getUserProperties().put(WebsocketConfig.LAST_TIME_NAME, System.currentTimeMillis());
        this.exec(text, session, false);
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
        Object o = session.getUserProperties().get(WebsocketConfig.LAST_TIME_NAME);
        if (o != null) {
            sid = (String) o;
            this.sessionManager.remove(sid);
        }
        this.logger.debug("wobsocket-on close:{}", sid);
    }

    @OnError
    public void onError(Throwable t, Session session) throws IOException {
        session.close();
        this.logger.error("wobsocket-on onerror:{}", t.getMessage());
    }
}
