package com.wolf.framework.websocket;

import com.sun.grizzly.websockets.DefaultWebSocket;
import com.sun.grizzly.websockets.ProtocolHandler;
import com.sun.grizzly.websockets.WebSocketListener;

/**
 *
 * @author aladdin
 */
public final class GlobalWebSocketImpl extends DefaultWebSocket implements GlobalWebSocket {

    private String sid;

    public GlobalWebSocketImpl(ProtocolHandler protocolHandler, WebSocketListener... listeners) {
        super(protocolHandler, listeners);
    }

    @Override
    public String toString() {
        return "sid:" + this.sid + super.toString();
    }

    @Override
    public String getSessionId() {
        return this.sid;
    }

    @Override
    public void setSessionId(String sid) {
        this.sid = sid;
    }
}
