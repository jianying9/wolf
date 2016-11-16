package com.wolf.framework.worker.workhandler;

import com.wolf.framework.service.Service;
import com.wolf.framework.service.context.ServiceContext;
import com.wolf.framework.service.parameter.ResponseParameterHandler;
import com.wolf.framework.service.request.ObjectRequestImpl;
import com.wolf.framework.service.response.ObjectResponseImpl;
import com.wolf.framework.worker.context.WorkerContext;
import java.util.Map;
import com.wolf.framework.service.response.ObjectResponse;
import com.wolf.framework.service.request.ObjectRequest;

/**
 * 默认处理类
 *
 * @author aladdin
 */
public class ObjectServiceWorkHandlerImpl implements WorkHandler {

    private final Service service;
    private final ServiceContext serviceContext;

    public ObjectServiceWorkHandlerImpl(final Service service, ServiceContext serviceContext) {
        this.service = service;
        this.serviceContext = serviceContext;
    }

    @Override
    public void execute(WorkerContext workerContext) {
        ObjectRequest objectRequest = new ObjectRequestImpl(workerContext.getWorkerRequest());
        String[] returnParameter = this.serviceContext.returnParameter();
        Map<String, ResponseParameterHandler> parameterHandlerMap = this.serviceContext.responseParameterHandlerMap();
        ObjectResponse objectResponse = new ObjectResponseImpl(workerContext.getWorkerResponse(), returnParameter, parameterHandlerMap);
        this.service.execute(objectRequest, objectResponse);
        String dataMessage = objectResponse.getDataMessage();
        workerContext.getWorkerResponse().setDataMessage(dataMessage);
    }
}
