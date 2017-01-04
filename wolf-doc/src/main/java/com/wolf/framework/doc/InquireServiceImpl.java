package com.wolf.framework.doc;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.service.ListService;
import com.wolf.framework.service.ServiceConfig;
import com.wolf.framework.service.context.ServiceContext;
import com.wolf.framework.service.parameter.ResponseConfig;
import com.wolf.framework.service.parameter.ResponseDataType;
import com.wolf.framework.worker.ServiceWorker;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import com.wolf.framework.service.response.ListResponse;
import com.wolf.framework.service.request.ListRequest;

/**
 *
 * @author jianying9
 */
@ServiceConfig(
        route = "/wolf/service",
        requestConfigs = {},
        responseConfigs = {
            @ResponseConfig(name = "routeName", dataType = ResponseDataType.STRING, filterTypes = {}, desc = ""),
            @ResponseConfig(name = "validateSession", dataType = ResponseDataType.BOOLEAN, desc = ""),
            @ResponseConfig(name = "hasAsyncResponse", dataType = ResponseDataType.BOOLEAN, desc = ""),
            @ResponseConfig(name = "page", dataType = ResponseDataType.BOOLEAN, desc = ""),
            @ResponseConfig(name = "list", dataType = ResponseDataType.BOOLEAN, desc = ""),
            @ResponseConfig(name = "desc", dataType = ResponseDataType.STRING, desc = "")
        },
        responseCodes = {},
        validateSession = false,
        desc = "")
public class InquireServiceImpl implements ListService {

    @Override
    public void execute(ListRequest listRequest, ListResponse listResponse) {
        Map<String, ServiceWorker> serviceWorkerMap = ApplicationContext.CONTEXT.getServiceWorkerMap();
        Set<Map.Entry<String, ServiceWorker>> entrySet = serviceWorkerMap.entrySet();
        //过滤系统接口
        List<ServiceWorker> serviceWorkerList = new ArrayList<>(serviceWorkerMap.size());
        ServiceContext serviceContext;
        for (Entry<String, ServiceWorker> entryService : entrySet) {
            serviceContext = entryService.getValue().getServiceContext();
            if (serviceContext.route().indexOf("/wolf") != 0) {
                serviceWorkerList.add(entryService.getValue());
            }
        }
        //排序
        Collections.sort(serviceWorkerList, new ServiceWorkerSort());
        //输出
        Map<String, Object> resultMap;
        List<Map<String, Object>> resultMapList = new ArrayList<>(serviceWorkerList.size());
        for (ServiceWorker serviceWorker : serviceWorkerList) {
            serviceContext = serviceWorker.getServiceContext();
            resultMap = new HashMap<>(4, 1);
            resultMap.put("routeName", serviceContext.route());
            resultMap.put("desc", serviceContext.desc());
            resultMap.put("validateSession", serviceContext.validateSession());
            resultMap.put("hasAsyncResponse", serviceContext.hasAsyncResponse());
            resultMap.put("page", serviceContext.page());
            resultMap.put("list", serviceContext.isList());
            resultMapList.add(resultMap);
        }
        listResponse.setDataMapList(resultMapList);
        listResponse.success();
    }

    private class ServiceWorkerSort implements Comparator<ServiceWorker> {

        @Override
        public int compare(ServiceWorker o1, ServiceWorker o2) {
            ServiceContext s1 = o1.getServiceContext();
            ServiceContext s2 = o2.getServiceContext();
            int result = s1.route().compareTo(s2.route());
            return result;
        }
    }
}
