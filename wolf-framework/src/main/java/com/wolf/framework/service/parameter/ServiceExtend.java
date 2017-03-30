package com.wolf.framework.service.parameter;

import java.util.List;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public class ServiceExtend {
    private final Map<String, List<RequestInfo>> requestInfoMap;
    private final Map<String, List<ResponseInfo>> responseInfoMap;

    public ServiceExtend(Map<String, List<RequestInfo>> requestInfoMap, Map<String, List<ResponseInfo>> responseInfoMap) {
        this.requestInfoMap = requestInfoMap;
        this.responseInfoMap = responseInfoMap;
    }

    public List<RequestInfo> getRequestExtend(String name) {
        name = name.toLowerCase().replace("_", "");
        return requestInfoMap.get(name);
    }

    public List<ResponseInfo> getResponseExtend(String name) {
        name = name.toLowerCase().replace("_", "");
        return responseInfoMap.get(name);
    }
    
}
