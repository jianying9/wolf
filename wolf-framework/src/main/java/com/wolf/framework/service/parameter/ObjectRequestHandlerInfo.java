package com.wolf.framework.service.parameter;

import java.util.Map;

/**
 *
 * @author jianying9
 */
public class ObjectRequestHandlerInfo {
    
    private final String[] requiredParameter;
    private final String[] unrequiredParameter;
    private final Map<String, RequestHandler> requestHandlerMap;

    public ObjectRequestHandlerInfo(String[] requiredParameter, String[] unrequiredParameter, Map<String, RequestHandler> requestHandlerMap) {
        this.requiredParameter = requiredParameter;
        this.unrequiredParameter = unrequiredParameter;
        this.requestHandlerMap = requestHandlerMap;
    }

    public String[] getRequiredParameter() {
        return requiredParameter;
    }

    public String[] getUnrequiredParameter() {
        return unrequiredParameter;
    }

    public Map<String, RequestHandler> getRequestHandlerMap() {
        return requestHandlerMap;
    }
    
}
