package com.wolf.framework.service.parameter;

import java.util.List;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public class ServiceExtendContext {
    private final Map<String, List<RequestInfo>> requestInfoMap;
    private final Map<String, List<ResponseInfo>> responseInfoMap;

    public ServiceExtendContext(Map<String, List<RequestInfo>> requestInfoMap, Map<String, List<ResponseInfo>> responseInfoMap) {
        this.requestInfoMap = requestInfoMap;
        this.responseInfoMap = responseInfoMap;
    }

    public List<RequestInfo> getRequestExtend(String name) {
        return requestInfoMap.get(name);
    }

    public List<ResponseInfo> getResponseExtend(String name) {
        return responseInfoMap.get(name);
    }
    
}
