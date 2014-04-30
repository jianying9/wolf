package com.wolf.framework.comet;

import com.wolf.framework.session.Session;

/**
 *
 * @author jianying9
 */
public interface LeaveEventHandler {
    
    public void execute(Session session, CometContext cometContext);
}
