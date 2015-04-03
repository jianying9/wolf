package com.wolf.framework.doc;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.data.DataType;
import com.wolf.framework.service.Service;
import com.wolf.framework.service.ServiceConfig;
import com.wolf.framework.service.parameter.RequestConfig;
import com.wolf.framework.service.parameter.ResponseConfig;
import com.wolf.framework.worker.ServiceWorker;
import com.wolf.framework.worker.context.MessageContext;
import java.util.ArrayList;
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
        requestConfigs = {
    @RequestConfig(name = "groupName", dataType = DataType.CHAR, max = 200, desc = "")
},
        responseConfigs = {
    @ResponseConfig(name = "routeNmae", dataType = DataType.CHAR, desc = ""),
    @ResponseConfig(name = "desc", dataType = DataType.CHAR, desc = "")
},
        responseStates = {},
        validateSession = false,
        page = true,
        group = "WOLF_FRAMEWORK",
        desc = "")
public class InquireServiceImpl implements Service {

    @Override
    public void execute(MessageContext messageContext) {
        String groupName = messageContext.getParameter("groupName");
        Map<String, ServiceWorker> serviceWorkerMap = ApplicationContext.CONTEXT.getServiceWorkerMap();
        Set<Map.Entry<String, ServiceWorker>> entrySet = serviceWorkerMap.entrySet();
        Map<String, String> resultMap;
        List<Map<String, String>> resultMapList = new ArrayList<Map<String, String>>(10);
        ServiceWorker serviceWorker;
        for (Entry<String, ServiceWorker> entryService : entrySet) {
            serviceWorker = entryService.getValue();
            if (serviceWorker.getGroup().equals(groupName)) {
                resultMap = new HashMap<String, String>(2, 1);
                resultMap.put("routeNmae", entryService.getKey());
                resultMap.put("desc", serviceWorker.getDescription());
                resultMapList.add(resultMap);
            }
        }
        messageContext.setMapListData(resultMapList);
        messageContext.success();
    }
}
