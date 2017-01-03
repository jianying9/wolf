package com.wolf.framework.service.parameter;

import java.util.Map;

/**
 *
 * @author jianying9
 */
public class ObjectRequestHandlerInfo {
    
    private final String[] requiredParameter;
    private final String[] unrequiredParameter;
    private final Map<String, RequestParameterHandler> requestParameterHandlerMap;

    public ObjectRequestHandlerInfo(String[] requiredParameter, String[] unrequiredParameter, Map<String, RequestParameterHandler> requestParameterHandlerMap) {
        this.requiredParameter = requiredParameter;
        this.unrequiredParameter = unrequiredParameter;
        this.requestParameterHandlerMap = requestParameterHandlerMap;
    }

    public String[] getRequiredParameter() {
        return requiredParameter;
    }

    public String[] getUnrequiredParameter() {
        return unrequiredParameter;
    }

    public Map<String, RequestParameterHandler> getRequestParameterHandlerMap() {
        return requestParameterHandlerMap;
    }
    
}
