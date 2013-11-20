package com.wolf.framework.websocket;

import com.sun.grizzly.tcp.Request;
import com.sun.grizzly.websockets.DataFrame;
import com.sun.grizzly.websockets.ProtocolHandler;
import com.sun.grizzly.websockets.WebSocket;
import com.sun.grizzly.websockets.WebSocketApplication;
import com.sun.grizzly.websockets.WebSocketListener;
import com.wolf.framework.config.FrameworkLoggerEnum;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.session.Session;
import com.wolf.framework.worker.ServiceWorker;
import com.wolf.framework.worker.context.FrameworkMessageContext;
import com.wolf.framework.worker.context.WebSocketMessageContextImpl;
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
public final class GlobalApplication extends WebSocketApplication {

    private final ConcurrentHashMap<String, GlobalWebSocket> webSockets = new ConcurrentHashMap<String, GlobalWebSocket>(32767, 1);
    private final Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.FRAMEWORK);
    private final Pattern actPattern = Pattern.compile("(?:\"act\":\")([A-Z_]+)(?:\")");

    @Override
    public WebSocket createWebSocket(ProtocolHandler protocolHandler, WebSocketListener... listeners) {
        return new GlobalWebSocketImpl(protocolHandler, listeners);
    }

    @Override
    public boolean isApplicationRequest(Request request) {
        final String uri = request.requestURI().toString();
        return uri.endsWith("/socket.io");
    }

    @Override
    public void onConnect(WebSocket socket) {
    }

    @Override
    public void onClose(WebSocket socket, DataFrame frame) {
        GlobalWebSocket globalWebSocket = (GlobalWebSocket) socket;
        Session session = globalWebSocket.getSession();
        if (session != null) {
            this.webSockets.remove(session.getUserId());
        }
        this.logger.debug("online count when close:{}", this.webSockets.size());
        socket.close();
    }

    @Override
    public void onMessage(WebSocket socket, String text) {
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
                FrameworkMessageContext frameworkMessageContext = new WebSocketMessageContextImpl(this, globalWebSocket, act, text);
                serviceWorker.doWork(frameworkMessageContext);
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
        String userId = session.getUserId();
        GlobalWebSocket other = this.webSockets.get(userId);
        if (other != null) {
            //该用户已经在其他地方登录，强退
            other.send("{\"flag\":\"SUCCESS\",\"act\":\"FORCED_LOGOUT\",\"data\":[]}");
            other.close();
        }
        this.webSockets.put(session.getUserId(), globalWebSocket);
        this.logger.debug("online count when save:{}", this.webSockets.size());
    }

    public GlobalWebSocket getGlobalWebSocket(String userId) {
        return this.webSockets.get(userId);
    }

    public void removGlobalWebSocket(GlobalWebSocket globalWebSocket) {
        this.webSockets.remove(globalWebSocket.getSession().getUserId());
    }

    public void shutdown() {
        for (GlobalWebSocket webSocket : this.webSockets.values()) {
            if (webSocket.isConnected()) {
                webSocket.close();
            }
        }
        this.webSockets.clear();
    }
}
