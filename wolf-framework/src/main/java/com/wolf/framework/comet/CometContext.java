package com.wolf.framework.comet;

/**
 *
 * @author jianying9
 */
public interface CometContext {

    public void push(String sid, String message);
    
    public void addCometHandler(CometHandler cometHandler);
}
