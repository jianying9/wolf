package com.wolf.framework.worker.context;

import java.util.Map;

/**
 *
 * @author jianying9
 */
public interface Response {

    public String getState();

    public Map<String, String> getMapData();

    public String getResponseMessage();

    public String getResponseMessage(boolean useCache);
    
    public String getSessionId();
}
