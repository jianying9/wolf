package com.wolf.framework.websocket;

import com.sun.grizzly.tcp.Request;
import com.sun.grizzly.websockets.DataFrame;
import com.sun.grizzly.websockets.ProtocolHandler;
import com.sun.grizzly.websockets.WebSocket;
import com.sun.grizzly.websockets.WebSocketApplication;
import com.sun.grizzly.websockets.WebSocketListener;
import com.wolf.framework.comet.CometHandler;
import com.wolf.framework.config.FrameworkLoggerEnum;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.logger.LogFactory;
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

    private final ConcurrentHashMap<String, GlobalWebSocket> webSockets;
    private final Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.FRAMEWORK);
    private final Pattern routePattern = Pattern.compile("(?:\"route\":\")([A-Z_]+)(?:\")");
    private final Pattern wolfPattern = Pattern.compile("(?:\"wolf\":\")([A-Z_]+)(?:\")");
    private final String pathEnd;

    public GlobalApplication(String appContextPath) {
        this.webSockets = new ConcurrentHashMap<String, GlobalWebSocket>(4096, 1);
        this.pathEnd = appContextPath.concat("/server.io");
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
        String sid = globalWebSocket.getSessionId();
        if (sid != null) {
            this.webSockets.remove(sid);
        }
        socket.close();
    }

    @Override
    public void onMessage(WebSocket socket, String text) {
        this.logger.debug("wobsocket: {}", text);
        GlobalWebSocket globalWebSocket = (GlobalWebSocket) socket;
        //获取act
        Matcher matcher = this.routePattern.matcher(text);
        if (matcher.find()) {
            String route = matcher.group(1);
            ServiceWorker serviceWorker = ApplicationContext.CONTEXT.getServiceWorker(route);
            if (serviceWorker == null) {
                //无效的act
                socket.send("{\"state\":\"INVALID\",\"error\":\"route not exist\"}");
            } else {
                //创建消息对象并执行服务
                WorkerContext workerContext = new WebSocketWorkerContextImpl(this, globalWebSocket, route, text);
                serviceWorker.doWork(workerContext);
                //返回消息
                String result = serviceWorker.getResponse().getResponseMessage();
                socket.send(result);
            }
        } else {
            matcher = this.wolfPattern.matcher(text);
            if (matcher.find()) {
                String wolf = matcher.group(1);
                if (wolf.equals("TIME")) {
                    //返回服务器时间
                    long time = System.currentTimeMillis();
                    StringBuilder resultBuilder = new StringBuilder(36);
                    resultBuilder.append("{\"wolf\":\"TIME\",\"time\":").append(Long.toString(time)).append('}');
                    socket.send(resultBuilder.toString());
                }
            }
        }
        //如果改socket没有session，则关闭
        String sid = globalWebSocket.getSessionId();
        if (sid == null) {
            globalWebSocket.close();
        }
    }

    public Map<String, GlobalWebSocket> getGlobalWebSockets() {
        return Collections.unmodifiableMap(this.webSockets);
    }

    public void removGlobalWebSocket(String sid) {
        this.webSockets.remove(sid);
        this.logger.debug("websocket remove session:{}", sid);
    }

    public synchronized void putGlobalWebSocket(GlobalWebSocket globalWebSocket) {
        String sid = globalWebSocket.getSessionId();
        GlobalWebSocket other = this.webSockets.get(sid);
        if (other != null) {
            //该用户已经在其他地方登录，强退
            other.send("{\"wolf\":\"CLOSE\"}");
            other.close();
        }
        this.webSockets.put(sid, globalWebSocket);
        this.logger.debug("websocket add session:{}", sid);
    }

    public GlobalWebSocket getGlobalWebSocket(String userId) {
        return this.webSockets.get(userId);
    }

    public void shutdown() {
        for (GlobalWebSocket webSocket : this.webSockets.values()) {
            if (webSocket.isConnected()) {
                webSocket.send("{\"wolf\":\"SHUTDOWN\"}");
                webSocket.close();
            }
        }
        this.webSockets.clear();
    }

    @Override
    public boolean push(String sid, String message) {
        this.logger.debug("websocket push message:{},{}", sid, message);
        boolean result = false;
        WebSocket webSocket = this.webSockets.get(sid);
        if (webSocket != null) {
            result = true;
            webSocket.send(message);
        } else {
            this.logger.debug("websocket push message:sid not exist:{}", sid);
        }
        return result;
    }
    
    @Override
    public String toString() {
        return "websokcet:".concat(super.toString());
    }
}
