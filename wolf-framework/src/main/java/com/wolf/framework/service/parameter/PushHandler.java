package com.wolf.framework.service.parameter;

import java.util.Map;

/**
 *
 * @author jianying9
 */
public class PushHandler {
    
    private final String route;
    
    private final String[] returnParameter;
    
    private final Map<String, ResponseParameterHandler> responseParameterHandlerMap;

    public PushHandler(String route, String[] returnParameter, Map<String, ResponseParameterHandler> responseParameterHandlerMap) {
        this.route = route;
        this.returnParameter = returnParameter;
        this.responseParameterHandlerMap = responseParameterHandlerMap;
    }

    public String getRoute() {
        return route;
    }

    public String[] getReturnParameter() {
        return returnParameter;
    }

    public Map<String, ResponseParameterHandler> getResponseParameterHandlerMap() {
        return responseParameterHandlerMap;
    }
    
}
