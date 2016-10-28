package com.wolf.framework.comet;

/**
 *
 * @author jianying9
 */
public interface CometHandler {

    public boolean push(String sid, String message);
    
    public boolean asyncPush(String sid, String message);
}
