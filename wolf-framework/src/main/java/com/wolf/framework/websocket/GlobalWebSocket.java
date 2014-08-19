package com.wolf.framework.websocket;

import com.sun.grizzly.websockets.WebSocket;

/**
 *
 * @author aladdin
 */
public interface GlobalWebSocket extends WebSocket {
    
    public String getSessionId();
    
    public void setSessionId(String sid);
}
