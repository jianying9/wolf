package com.wolf.framework.websocket;

import com.sun.grizzly.websockets.WebSocket;
import com.wolf.framework.session.Session;

/**
 *
 * @author aladdin
 */
public interface GlobalWebSocket extends WebSocket {

    public Session getSession();

    public void setSession(Session session);
}
