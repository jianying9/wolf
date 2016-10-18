package com.wolf.framework.worker.workhandler;

import com.wolf.framework.service.ListService;
import com.wolf.framework.service.context.ServiceContext;
import com.wolf.framework.service.parameter.ResponseParameterHandler;
import com.wolf.framework.service.request.ListServiceRequest;
import com.wolf.framework.service.request.ListServiceRequestImpl;
import com.wolf.framework.service.response.ListServiceResponse;
import com.wolf.framework.service.response.ListServiceResponseImpl;
import com.wolf.framework.worker.context.WorkerContext;
import java.util.Map;

/**
 * 默认处理类
 *
 * @author aladdin
 */
public class ListServiceWorkHandlerImpl implements WorkHandler {

    private final ListService listService;
    private final ServiceContext serviceContext;

    public ListServiceWorkHandlerImpl(final ListService listService, ServiceContext serviceContext) {
        this.listService = listService;
        this.serviceContext = serviceContext;
    }

    @Override
    public void execute(WorkerContext workerContext) {
        ListServiceRequest listServiceRequest = new ListServiceRequestImpl(workerContext.getWorkerRequest());
        String[] returnParameter = this.serviceContext.returnParameter();
        Map<String, ResponseParameterHandler> parameterHandlerMap = this.serviceContext.responseParameterHandlerMap();
        ListServiceResponse listServiceResponse = new ListServiceResponseImpl(workerContext.getWorkerResponse(), returnParameter, parameterHandlerMap, listServiceRequest);
        this.listService.execute(listServiceRequest, listServiceResponse);
        String dataMessage = listServiceResponse.getDataMessage();
        workerContext.getWorkerResponse().setDataMessage(dataMessage);
    }
}
