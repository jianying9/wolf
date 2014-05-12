package com.wolf.framework.doc;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.data.TypeEnum;
import com.wolf.framework.service.Service;
import com.wolf.framework.service.ServiceConfig;
import com.wolf.framework.service.parameter.InputConfig;
import com.wolf.framework.service.parameter.OutputConfig;
import com.wolf.framework.worker.ServiceWorker;
import com.wolf.framework.worker.context.MessageContext;

/**
 *
 * @author aladdin
 */
@ServiceConfig(
        actionName = "WOLF_INQUIRE_SERVICE_INFO",
        importantParameter = {
    @InputConfig(name = "actionName", typeEnum = TypeEnum.CHAR_255, desc = "")
},
        returnParameter = {
    @OutputConfig(name = "actionName", typeEnum = TypeEnum.CHAR_255, desc = ""),
    @OutputConfig(name = "description", typeEnum = TypeEnum.CHAR_255, desc = ""),
    @OutputConfig(name = "groupName", typeEnum = TypeEnum.CHAR_255, desc = ""),
    @OutputConfig(name = "importantParameter", typeEnum = TypeEnum.ARRAY, desc = ""),
    @OutputConfig(name = "minorParameter", typeEnum = TypeEnum.ARRAY, desc = ""),
    @OutputConfig(name = "returnParameter", typeEnum = TypeEnum.ARRAY, desc = "")
},
        validateSession = false,
        response = true,
        group = "WOLF_FRAMEWORK",
        description = "")
public class InquireInfoServiceImpl implements Service {
    
    @Override
    public void execute(MessageContext messageContext) {
        String actionName = messageContext.getParameter("actionName");
        ServiceWorker serviceWorker = ApplicationContext.CONTEXT.getServiceWorker(actionName);
        if (serviceWorker != null) {
            messageContext.setMapData(serviceWorker.getInfoMap());
            messageContext.success();
        }
    }
}
