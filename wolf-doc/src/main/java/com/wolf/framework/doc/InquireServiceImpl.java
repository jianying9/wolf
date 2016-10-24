package com.wolf.framework.doc;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.data.DataType;
import com.wolf.framework.service.ListService;
import com.wolf.framework.service.ServiceConfig;
import com.wolf.framework.service.parameter.ResponseConfig;
import com.wolf.framework.service.request.ListServiceRequest;
import com.wolf.framework.service.response.ListServiceResponse;
import com.wolf.framework.worker.ServiceWorker;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author aladdin
 */
@ServiceConfig(
        route = "/wolf/service",
        requestConfigs = {},
        responseConfigs = {
            @ResponseConfig(name = "routeName", dataType = DataType.CHAR, desc = ""),
            @ResponseConfig(name = "groupName", dataType = DataType.CHAR, desc = ""),
            @ResponseConfig(name = "desc", dataType = DataType.CHAR, desc = "")
        },
        responseCodes = {},
        validateSession = false,
        validateSecurity = false,
        group = "WOLF_FRAMEWORK",
        desc = "")
public class InquireServiceImpl implements ListService {

    @Override
    public void execute(ListServiceRequest listServiceRequest, ListServiceResponse listServiceResponse) {
        Map<String, ServiceWorker> serviceWorkerMap = ApplicationContext.CONTEXT.getServiceWorkerMap();
        Set<Map.Entry<String, ServiceWorker>> entrySet = serviceWorkerMap.entrySet();
        //过滤系统接口
        List<ServiceWorker> serviceWorkerList = new ArrayList<ServiceWorker>(serviceWorkerMap.size());
        ServiceWorker serviceWorker;
        for (Entry<String, ServiceWorker> entryService : entrySet) {
            serviceWorker = entryService.getValue();
            if (serviceWorker.getGroup().equals("WOLF_FRAMEWORK") == false) {
                serviceWorkerList.add(serviceWorker);
            }
        }
        //排序
        Collections.sort(serviceWorkerList, new ServiceWorkerSort());
        //输出
        Map<String, String> resultMap;
        List<Map<String, String>> resultMapList = new ArrayList<Map<String, String>>(serviceWorkerList.size());
        for (ServiceWorker sw : serviceWorkerList) {
            resultMap = new HashMap<String, String>(4, 1);
            resultMap.put("routeName", sw.getRoute());
            resultMap.put("groupName", sw.getGroup());
            resultMap.put("desc", sw.getDesc());
            resultMapList.add(resultMap);
        }
        listServiceResponse.setDataMapList(resultMapList);
        listServiceResponse.setNextSize(resultMapList.size());
        listServiceResponse.success();
    }

    private class ServiceWorkerSort implements Comparator<ServiceWorker> {

        @Override
        public int compare(ServiceWorker o1, ServiceWorker o2) {
            int result = o1.getGroup().compareTo(o2.getGroup());
            if (result == 0) {
                result = o1.getRoute().compareTo(o2.getRoute());
            }
            return result;
        }
    }
}
