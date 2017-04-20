package com.wolf.framework.service.parameter;

import java.util.Map;

/**
 *
 * @author jianying9
 */
public class ServicePushContext {
    private final Map<String, PushInfo> pushInfoMap;
    private final Map<String, PushHandler> pushHandlerMap;

    public ServicePushContext(Map<String, PushInfo> pushInfoMap, Map<String, PushHandler> pushHandlerMap) {
        this.pushInfoMap = pushInfoMap;
        this.pushHandlerMap = pushHandlerMap;
    }
    
    public Map<String, PushInfo> getPushInfoMap() {
        return this.pushInfoMap;
    }
    
    public PushInfo getPushInfo(String name) {
        return pushInfoMap.get(name);
    }

    public PushHandler getPushHandler(String name) {
        return pushHandlerMap.get(name);
    }
    
}
