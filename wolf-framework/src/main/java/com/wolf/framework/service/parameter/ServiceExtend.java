package com.wolf.framework.service.parameter;

import java.util.List;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public class ServiceExtend {
    private final Map<String, List<RequestConfig>> requestExtendMap;
    private final Map<String, List<ResponseConfig>> responseExtendMap;

    public ServiceExtend(Map<String, List<RequestConfig>> requestExtendMap, Map<String, List<ResponseConfig>> responseExtendMap) {
        this.requestExtendMap = requestExtendMap;
        this.responseExtendMap = responseExtendMap;
    }

    public List<RequestConfig> getRequestExtend(String name) {
        return requestExtendMap.get(name);
    }

    public List<ResponseConfig> getResponseExtend(String name) {
        return responseExtendMap.get(name);
    }
    
}
