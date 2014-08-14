package com.wolf.framework.comet;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jianying9
 */
public final class CometContextImpl implements CometContext {

    private final List<CometHandler> cometHanlderList = new ArrayList<CometHandler>(2);

    @Override
    public void push(String sid, String message) {
        for (CometHandler cometHandler : this.cometHanlderList) {
            cometHandler.push(sid, message);
        }
    }

    @Override
    public void addCometHandler(CometHandler cometHandler) {
        this.cometHanlderList.add(cometHandler);
    }
}
