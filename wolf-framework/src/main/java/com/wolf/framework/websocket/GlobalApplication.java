package com.wolf.framework.websocket;

import com.sun.grizzly.tcp.Request;
import com.sun.grizzly.websockets.DataFrame;
import com.sun.grizzly.websockets.ProtocolHandler;
import com.sun.grizzly.websockets.WebSocket;
import com.sun.grizzly.websockets.WebSocketApplication;
import com.sun.grizzly.websockets.WebSocketListener;
import com.wolf.framework.comet.CometContext;
import com.wolf.framework.comet.CometHandler;
import com.wolf.framework.config.FrameworkLoggerEnum;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.session.Session;
import com.wolf.framework.worker.ServiceWorker;
import com.wolf.framework.worker.context.WebSocketWorkerContextImpl;
import com.wolf.framework.worker.context.WorkerContext;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;

/**
 *
 * @author aladdin
 */
public final class GlobalApplication extends WebSocketApplication implements CometHandler {

    private final ConcurrentHashMap<String, GlobalWebSocket> webSockets = new ConcurrentHashMap<String, GlobalWebSocket>(4096, 1);
    private final Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.FRAMEWORK);
    private final Pattern actPattern = Pattern.compile("(?:\"act\":\")([A-Z_]+)(?:\")");
    private final String pathEnd;

    public GlobalApplication(String appContextPath) {
        this.pathEnd = appContextPath.concat("/service.io");
    }

    @Override
    public WebSocket createWebSocket(ProtocolHandler protocolHandler, WebSocketListener... listeners) {
        return new GlobalWebSocketImpl(protocolHandler, listeners);
    }

    @Override
    public boolean isApplicationRequest(Request request) {
        final String uri = request.requestURI().toString();
        return uri.endsWith(this.pathEnd);
    }

    @Override
    public void onConnect(WebSocket socket) {
    }

    @Override
    public void onClose(WebSocket socket, DataFrame frame) {
        GlobalWebSocket globalWebSocket = (GlobalWebSocket) socket;
        Session session = globalWebSocket.getSession();
        if (session != null) {
            this.webSockets.remove(session.getSid());
            //触发comet用户离开事件
            CometContext cometContext = ApplicationContext.CONTEXT.getCometContext();
            cometContext.invokeLeaveEvent(session);
        }
        socket.close();
    }

    @Override
    public void onMessage(WebSocket socket, String text) {
        this.logger.debug(text);
        GlobalWebSocket globalWebSocket = (GlobalWebSocket) socket;
        //获取act
        Matcher matcher = this.actPattern.matcher(text);
        if (matcher.find()) {
            String act = matcher.group(1);
            ServiceWorker serviceWorker = ApplicationContext.CONTEXT.getServiceWorker(act);
            if (serviceWorker == null) {
                this.logger.error("invalid act value:{}", act);
                this.logger.error("invalid json value:{}", text);
                //无效的act
                socket.send("{\"flag\":\"INVALID\",\"error\":\"act not exist\"}");
            } else {
                //创建消息对象并执行服务
                WorkerContext workerContext = new WebSocketWorkerContextImpl(this, globalWebSocket, act, text);
                serviceWorker.doWork(workerContext);
            }
        } else {
            socket.send("{\"flag\":\"EXCEPTION\",\"error\":\"error api\"}");
        }
        //如果改socket没有session，则关闭
        Session session = globalWebSocket.getSession();
        if (session == null) {
            globalWebSocket.close();
        }
    }

    public Map<String, GlobalWebSocket> getGlobalWebSockets() {
        return Collections.unmodifiableMap(this.webSockets);
    }

    public synchronized void putGlobalWebSocket(GlobalWebSocket globalWebSocket) {
        Session session = globalWebSocket.getSession();
        String sid = session.getSid();
        GlobalWebSocket other = this.webSockets.get(sid);
        if (other != null) {
            //该用户已经在其他地方登录，强退
            other.send("{\"flag\":\"SUCCESS\",\"act\":\"FORCED_LOGOUT\",\"data\":[]}");
            other.close();
        }
        this.webSockets.put(session.getSid(), globalWebSocket);
    }

    public GlobalWebSocket getGlobalWebSocket(String userId) {
        return this.webSockets.get(userId);
    }

    public void removGlobalWebSocket(GlobalWebSocket globalWebSocket) {
        this.webSockets.remove(globalWebSocket.getSession().getSid());
    }

    public void shutdown() {
        for (GlobalWebSocket webSocket : this.webSockets.values()) {
            if (webSocket.isConnected()) {
                webSocket.close();
            }
        }
        this.webSockets.clear();
    }

    @Override
    public void push(String sid, String message) {
        WebSocket webSocket = this.webSockets.get(sid);
        if (webSocket != null) {
            webSocket.send(message);
        }
    }
}
