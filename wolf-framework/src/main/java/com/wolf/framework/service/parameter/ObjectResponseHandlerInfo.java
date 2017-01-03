package com.wolf.framework.service.parameter;

import java.util.Map;

/**
 *
 * @author jianying9
 */
public class ObjectResponseHandlerInfo {
    
    private final String[] parameter;
    private final Map<String, ResponseParameterHandler> responseParameterHandlerMap;

    public ObjectResponseHandlerInfo(String[] parameter, Map<String, ResponseParameterHandler> responseParameterHandlerMap) {
        this.parameter = parameter;
        this.responseParameterHandlerMap = responseParameterHandlerMap;
    }

    public String[] getParameter() {
        return parameter;
    }

    public Map<String, ResponseParameterHandler> getResponseParameterHandlerMap() {
        return responseParameterHandlerMap;
    }

}
