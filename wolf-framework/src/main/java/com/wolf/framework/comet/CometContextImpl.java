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
    public boolean push(String sid, String message) {
        boolean result = false;
        boolean isPush;
        for (CometHandler cometHandler : this.cometHanlderList) {
            isPush = cometHandler.push(sid, message);
            if(isPush) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public void addCometHandler(CometHandler cometHandler) {
        this.cometHanlderList.add(cometHandler);
    }
}
