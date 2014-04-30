package com.wolf.framework.comet;

import com.wolf.framework.session.Session;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jianying9
 */
public final class CometContextImpl implements CometContext {
    
    private final List<CometHandler> cometHanlderList = new ArrayList<CometHandler>(2);
    private final List<LeaveEventHandler> leaveEventHandlerList = new ArrayList<LeaveEventHandler>(2);
    
    @Override
    public void push(String sid, String message) {
        for (CometHandler cometHandler : this.cometHanlderList) {
            cometHandler.push(sid, message);
        }
    }
    
    @Override
    public void invokeLeaveEvent(Session session) {
        for (LeaveEventHandler leaveEventHandler : this.leaveEventHandlerList) {
            leaveEventHandler.execute(session, this);
        }
    }
    
    @Override
    public void addLeaveEventHandler(LeaveEventHandler leaveEventHandler) {
        this.leaveEventHandlerList.add(leaveEventHandler);
    }
    
    @Override
    public void addCometHandler(CometHandler cometHandler) {
        this.cometHanlderList.add(cometHandler);
    }
}
