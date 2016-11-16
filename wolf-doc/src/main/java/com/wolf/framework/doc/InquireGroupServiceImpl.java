package com.wolf.framework.doc;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.data.DataType;
import com.wolf.framework.service.ListService;
import com.wolf.framework.service.ServiceConfig;
import com.wolf.framework.service.context.ServiceContext;
import com.wolf.framework.service.parameter.ResponseConfig;
import com.wolf.framework.worker.ServiceWorker;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.wolf.framework.service.response.ListResponse;
import com.wolf.framework.service.request.ListRequest;

/**
 *
 * @author jianying9
 */
@ServiceConfig(
        route = "/wolf/group",
        responseConfigs = {
            @ResponseConfig(name = "groupName", dataType = DataType.STRING, desc = "")
        },
        responseCodes = {},
        validateSession = false,
        desc = "")
public class InquireGroupServiceImpl implements ListService {

    @Override
    public void execute(ListRequest listRequest, ListResponse listResponse) {
        Map<String, ServiceWorker> serviceWorkerMap = ApplicationContext.CONTEXT.getServiceWorkerMap();
        Set<Map.Entry<String, ServiceWorker>> entrySet = serviceWorkerMap.entrySet();
        Map<String, String> resultMap;
        List<Map<String, String>> resultMapList = new ArrayList<>(10);
        ServiceContext serviceContext;
        Set<String> groupNameSet = new HashSet<>(serviceWorkerMap.size(), 1);
        for (Map.Entry<String, ServiceWorker> entryService : entrySet) {
            serviceContext = entryService.getValue().getServiceContext();
            groupNameSet.add(serviceContext.group());
        }
        groupNameSet.remove("wolf");
        for (String groupName : groupNameSet) {
            resultMap = new HashMap<>(2, 1);
            resultMap.put("groupName", groupName);
            resultMapList.add(resultMap);
        }
        listResponse.setDataMapList(resultMapList);
        listResponse.setNextSize(resultMapList.size());
        listResponse.success();
    }
}
