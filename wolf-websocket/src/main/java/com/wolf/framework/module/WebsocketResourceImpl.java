package com.wolf.framework.module;

import com.sun.grizzly.websockets.WebSocketEngine;
import com.wolf.framework.context.Resource;
import com.wolf.framework.websocket.GlobalApplication;

/**
 *
 * @author jianying9
 */
public class WebsocketResourceImpl implements Resource {

    private final GlobalApplication app;

    public WebsocketResourceImpl(GlobalApplication app) {
        this.app = app;
    }

    @Override
    public void destory() {
        WebSocketEngine.getEngine().unregister(this.app);
    }
}
