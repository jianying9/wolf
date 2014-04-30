package com.wolf.framework.comet;

import com.wolf.framework.session.Session;

/**
 *
 * @author jianying9
 */
public interface CometContext {

    public void push(String sid, String message);
    
    public void invokeLeaveEvent(Session session);
    
    public void addCometHandler(CometHandler cometHandler);
    
    public void addLeaveEventHandler(LeaveEventHandler leaveEventHandler);
}
