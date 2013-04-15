package com.wolf.framework.websocket;

import com.sun.grizzly.websockets.DefaultWebSocket;
import com.sun.grizzly.websockets.ProtocolHandler;
import com.sun.grizzly.websockets.WebSocketListener;
import com.wolf.framework.session.Session;

/**
 *
 * @author aladdin
 */
public final class GlobalWebSocketImpl extends DefaultWebSocket implements GlobalWebSocket {

    private Session session;

    public GlobalWebSocketImpl(ProtocolHandler protocolHandler, WebSocketListener... listeners) {
        super(protocolHandler, listeners);
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public void setSession(Session session) {
        this.session = session;
    }
}
