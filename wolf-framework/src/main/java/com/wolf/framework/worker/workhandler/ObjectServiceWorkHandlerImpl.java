package com.wolf.framework.worker.workhandler;

import com.wolf.framework.service.Service;
import com.wolf.framework.service.context.ServiceContext;
import com.wolf.framework.service.parameter.ResponseParameterHandler;
import com.wolf.framework.service.request.ServiceRequest;
import com.wolf.framework.service.request.ServiceRequestImpl;
import com.wolf.framework.service.response.ServiceResponse;
import com.wolf.framework.service.response.ServiceResponseImpl;
import com.wolf.framework.worker.context.WorkerContext;
import java.util.Map;

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
        ServiceRequest serviceRequest = new ServiceRequestImpl(workerContext.getWorkerRequest());
        String[] returnParameter = this.serviceContext.returnParameter();
        Map<String, ResponseParameterHandler> parameterHandlerMap = this.serviceContext.responseParameterHandlerMap();
        ServiceResponse serviceResponse = new ServiceResponseImpl(workerContext.getWorkerResponse(), returnParameter, parameterHandlerMap);
        this.service.execute(serviceRequest, serviceResponse);
    }
}
