package com.wolf.framework.service.parameter;

import java.util.Map;

/**
 *
 * @author jianying9
 */
public class PushHandler {
    
    private final String route;
    
    private final String[] returnParameter;
    
    private final Map<String, ResponseHandler> responseHandlerMap;

    public PushHandler(String route, String[] returnParameter, Map<String, ResponseHandler> responseHandlerMap) {
        this.route = route;
        this.returnParameter = returnParameter;
        this.responseHandlerMap = responseHandlerMap;
    }

    public String getRoute() {
        return route;
    }

    public String[] getReturnParameter() {
        return returnParameter;
    }

    public Map<String, ResponseHandler> getResponseHandlerMap() {
        return responseHandlerMap;
    }
    
}
