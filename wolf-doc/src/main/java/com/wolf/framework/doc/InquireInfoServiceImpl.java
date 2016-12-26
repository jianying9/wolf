package com.wolf.framework.doc;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.service.Service;
import com.wolf.framework.service.ServiceConfig;
import com.wolf.framework.service.parameter.RequestConfig;
import com.wolf.framework.service.parameter.RequestDataType;
import com.wolf.framework.service.parameter.ResponseConfig;
import com.wolf.framework.service.parameter.ResponseDataType;
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
            @RequestConfig(name = "routeName", dataType = RequestDataType.STRING, max = 200, desc = "")
        },
        responseConfigs = {
            @ResponseConfig(name = "routeName", dataType = ResponseDataType.STRING, desc = ""),
            @ResponseConfig(name = "desc", dataType = ResponseDataType.STRING, desc = ""),
            @ResponseConfig(name = "page", dataType = ResponseDataType.BOOLEAN, desc = ""),
            @ResponseConfig(name = "validateSession", dataType = ResponseDataType.BOOLEAN, desc = ""),
            @ResponseConfig(name = "groupName", dataType = ResponseDataType.STRING, desc = ""),
            @ResponseConfig(name = "requestConfigs", dataType = ResponseDataType.OBJECT_ARRAY, desc = ""),
            @ResponseConfig(name = "responseCodes", dataType = ResponseDataType.OBJECT_ARRAY, desc = ""),
            @ResponseConfig(name = "responseConfigs", dataType = ResponseDataType.OBJECT_ARRAY, desc = ""),
            @ResponseConfig(name = "hasAsyncResponse", dataType = ResponseDataType.BOOLEAN, desc = "")
        },
        responseCodes = {},
        validateSession = false,
        desc = "")
public class InquireInfoServiceImpl implements Service {

    @Override
    public void execute(ObjectRequest objectRequest, ObjectResponse objectResponse) {
        String route = objectRequest.getStringValue("routeName");
        ServiceWorker serviceWorker = ApplicationContext.CONTEXT.getServiceWorker(route);
        if (serviceWorker != null) {
            objectResponse.setNewSessionId(UUID.randomUUID().toString());
            objectResponse.setDataMap(serviceWorker.getInfoMap());
            objectResponse.success();
        }
    }
}
