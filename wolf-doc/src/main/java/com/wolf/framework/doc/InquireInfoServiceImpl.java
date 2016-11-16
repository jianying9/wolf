package com.wolf.framework.doc;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.data.DataType;
import com.wolf.framework.service.Service;
import com.wolf.framework.service.ServiceConfig;
import com.wolf.framework.service.parameter.RequestConfig;
import com.wolf.framework.service.parameter.ResponseConfig;
import com.wolf.framework.worker.ServiceWorker;
import java.util.UUID;
import com.wolf.framework.service.response.ObjectResponse;
import com.wolf.framework.service.request.ObjectRequest;

/**
 *
 * @author jianying9
 */
@ServiceConfig(
        route = "/wolf/service/info",
        requestConfigs = {
            @RequestConfig(name = "routeName", dataType = DataType.STRING, max = 200, desc = "")
        },
        responseConfigs = {
            @ResponseConfig(name = "routeName", dataType = DataType.STRING, desc = ""),
            @ResponseConfig(name = "desc", dataType = DataType.STRING, desc = ""),
            @ResponseConfig(name = "page", dataType = DataType.BOOLEAN, desc = ""),
            @ResponseConfig(name = "validateSession", dataType = DataType.BOOLEAN, desc = ""),
            @ResponseConfig(name = "groupName", dataType = DataType.STRING, desc = ""),
            @ResponseConfig(name = "requestConfigs", dataType = DataType.ARRAY, desc = ""),
            @ResponseConfig(name = "responseCodes", dataType = DataType.ARRAY, desc = ""),
            @ResponseConfig(name = "responseConfigs", dataType = DataType.ARRAY, desc = ""),
            @ResponseConfig(name = "hasAsyncResponse", dataType = DataType.BOOLEAN, desc = "")
        },
        responseCodes = {},
        validateSession = false,
        desc = "")
public class InquireInfoServiceImpl implements Service {

    @Override
    public void execute(ObjectRequest objectRequest, ObjectResponse objectResponse) {
        String route = objectRequest.getParameter("routeName");
        ServiceWorker serviceWorker = ApplicationContext.CONTEXT.getServiceWorker(route);
        if (serviceWorker != null) {
            objectResponse.setNewSessionId(UUID.randomUUID().toString());
            objectResponse.setDataMap(serviceWorker.getInfoMap());
            objectResponse.success();
        }
    }
}
