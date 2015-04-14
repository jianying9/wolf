package com.wolf.framework.doc;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.data.DataType;
import com.wolf.framework.service.Service;
import com.wolf.framework.service.ServiceConfig;
import com.wolf.framework.service.parameter.RequestConfig;
import com.wolf.framework.service.parameter.ResponseConfig;
import com.wolf.framework.worker.ServiceWorker;
import com.wolf.framework.worker.context.MessageContext;
import java.util.UUID;

/**
 *
 * @author aladdin
 */
@ServiceConfig(
        route = "/wolf/service/info",
        requestConfigs = {
    @RequestConfig(name = "routeName", dataType = DataType.CHAR, max = 200, desc = "")
},
        responseConfigs = {
    @ResponseConfig(name = "routeName", dataType = DataType.CHAR, desc = ""),
    @ResponseConfig(name = "desc", dataType = DataType.CHAR, desc = ""),
    @ResponseConfig(name = "page", dataType = DataType.BOOLEAN, desc = ""),
    @ResponseConfig(name = "validateSession", dataType = DataType.BOOLEAN, desc = ""),
    @ResponseConfig(name = "groupName", dataType = DataType.CHAR, desc = ""),
    @ResponseConfig(name = "requestConfigs", dataType = DataType.ARRAY, desc = ""),
    @ResponseConfig(name = "responseStates", dataType = DataType.ARRAY, desc = ""),
    @ResponseConfig(name = "responseConfigs", dataType = DataType.ARRAY, desc = "")
},
        responseStates = {},
        validateSession = false,
        validateSecurity = false,
        group = "WOLF_FRAMEWORK",
        desc = "")
public class InquireInfoServiceImpl implements Service {

    @Override
    public void execute(MessageContext messageContext) {
        String route = messageContext.getParameter("routeName");
        ServiceWorker serviceWorker = ApplicationContext.CONTEXT.getServiceWorker(route);
        if (serviceWorker != null) {
            messageContext.setNewSessionId(UUID.randomUUID().toString());
            messageContext.setMapData(serviceWorker.getInfoMap());
            messageContext.success();
        }
    }
}
