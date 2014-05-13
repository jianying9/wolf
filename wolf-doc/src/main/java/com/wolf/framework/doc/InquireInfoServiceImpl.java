package com.wolf.framework.doc;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.data.TypeEnum;
import com.wolf.framework.service.Service;
import com.wolf.framework.service.ServiceConfig;
import com.wolf.framework.service.SessionHandleTypeEnum;
import com.wolf.framework.service.parameter.RequestConfig;
import com.wolf.framework.service.parameter.ResponseConfig;
import com.wolf.framework.session.Session;
import com.wolf.framework.session.SessionImpl;
import com.wolf.framework.worker.ServiceWorker;
import com.wolf.framework.worker.context.MessageContext;
import java.util.UUID;

/**
 *
 * @author aladdin
 */
@ServiceConfig(
        actionName = "WOLF_INQUIRE_SERVICE_INFO",
        requestConfigs = {
    @RequestConfig(name = "actionName", typeEnum = TypeEnum.CHAR_255, desc = "")
},
        responseConfigs = {
    @ResponseConfig(name = "actionName", typeEnum = TypeEnum.CHAR_255, desc = ""),
    @ResponseConfig(name = "desc", typeEnum = TypeEnum.CHAR_255, desc = ""),
    @ResponseConfig(name = "groupName", typeEnum = TypeEnum.CHAR_255, desc = ""),
    @ResponseConfig(name = "requestConfigs", typeEnum = TypeEnum.ARRAY, desc = ""),
    @ResponseConfig(name = "responseConfigs", typeEnum = TypeEnum.ARRAY, desc = "")
},
        validateSession = false,
        sessionHandleTypeEnum = SessionHandleTypeEnum.SAVE,
        response = true,
        group = "WOLF_FRAMEWORK",
        description = "")
public class InquireInfoServiceImpl implements Service {
    
    @Override
    public void execute(MessageContext messageContext) {
        String actionName = messageContext.getParameter("actionName");
        ServiceWorker serviceWorker = ApplicationContext.CONTEXT.getServiceWorker(actionName);
        if (serviceWorker != null) {
            Session session = new SessionImpl(UUID.randomUUID().toString());
            messageContext.setNewSession(session);
            messageContext.setMapData(serviceWorker.getInfoMap());
            messageContext.success();
        }
    }
}
