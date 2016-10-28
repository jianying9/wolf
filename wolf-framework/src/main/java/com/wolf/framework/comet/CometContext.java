package com.wolf.framework.comet;

/**
 *
 * @author jianying9
 */
public interface CometContext {

    public boolean push(String sid, String message);
    
    public boolean asyncPush(String sid, String message);
    
    public void addCometHandler(CometHandler cometHandler);
}
