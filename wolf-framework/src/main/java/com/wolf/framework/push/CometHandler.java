package com.wolf.framework.push;

/**
 *
 * @author jianying9
 */
public interface CometHandler {

    public boolean push(String sid, String route, String message);
    
    public boolean asyncPush(String sid, String route, String message);
}
