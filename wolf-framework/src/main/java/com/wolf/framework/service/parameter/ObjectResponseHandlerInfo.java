package com.wolf.framework.service.parameter;

import java.util.Map;

/**
 *
 * @author jianying9
 */
public class ObjectResponseHandlerInfo {
    
    private final String[] parameter;
    private final Map<String, ResponseHandler> responseParameterHandlerMap;

    public ObjectResponseHandlerInfo(String[] parameter, Map<String, ResponseHandler> responseParameterHandlerMap) {
        this.parameter = parameter;
        this.responseParameterHandlerMap = responseParameterHandlerMap;
    }

    public String[] getParameter() {
        return parameter;
    }

    public Map<String, ResponseHandler> getResponseParameterHandlerMap() {
        return responseParameterHandlerMap;
    }

}
