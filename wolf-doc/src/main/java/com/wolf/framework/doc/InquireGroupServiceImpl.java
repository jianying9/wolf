package com.wolf.framework.doc;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.data.DataType;
import com.wolf.framework.service.Service;
import com.wolf.framework.service.ServiceConfig;
import com.wolf.framework.service.parameter.ResponseConfig;
import com.wolf.framework.worker.ServiceWorker;
import com.wolf.framework.worker.context.MessageContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author aladdin
 */
@ServiceConfig(
        route = "/wolf/group",
        responseConfigs = {
            @ResponseConfig(name = "groupName", dataType = DataType.CHAR, desc = "")
        },
        responseStates = {},
        validateSession = false,
        page = true,
        group = "WOLF_FRAMEWORK",
        desc = "")
public class InquireGroupServiceImpl implements Service {

    @Override
    public void execute(MessageContext messageContext) {
        Map<String, ServiceWorker> serviceWorkerMap = ApplicationContext.CONTEXT.getServiceWorkerMap();
        Set<Map.Entry<String, ServiceWorker>> entrySet = serviceWorkerMap.entrySet();
        Map<String, String> resultMap;
        List<Map<String, String>> resultMapList = new ArrayList<Map<String, String>>(10);
        ServiceWorker serviceWorker;
        Set<String> groupNameSet = new HashSet<String>(serviceWorkerMap.size(), 1);
        for (Map.Entry<String, ServiceWorker> entryService : entrySet) {
            serviceWorker = entryService.getValue();
            groupNameSet.add(serviceWorker.getGroup());
        }
        groupNameSet.remove("WOLF_FRAMEWORK");
        for (String groupName : groupNameSet) {
            resultMap = new HashMap<String, String>(2, 1);
            resultMap.put("groupName", groupName);
            resultMapList.add(resultMap);
        }
        messageContext.setMapListData(resultMapList);
        messageContext.success();
    }
}
