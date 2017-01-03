package com.wolf.framework.worker.workhandler;

import com.wolf.framework.service.ListService;
import com.wolf.framework.service.context.ServiceContext;
import com.wolf.framework.service.parameter.ResponseParameterHandler;
import com.wolf.framework.service.request.ListRequestImpl;
import com.wolf.framework.service.response.ListResponseImpl;
import com.wolf.framework.worker.context.WorkerContext;
import java.util.Map;
import com.wolf.framework.service.response.ListResponse;
import com.wolf.framework.service.request.ListRequest;

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
        boolean page = workerContext.getServiceWorker().getServiceContext().page();
        ListRequest listRequest = new ListRequestImpl(workerContext.getWorkerRequest());
        String[] returnParameter = this.serviceContext.returnParameter();
        Map<String, ResponseParameterHandler> parameterHandlerMap = this.serviceContext.responseParameterHandlerMap();
        ListResponse listResponse = new ListResponseImpl(page, workerContext.getWorkerResponse(), returnParameter, parameterHandlerMap, listRequest);
        this.listService.execute(listRequest, listResponse);
        String dataMessage = listResponse.getDataMessage();
        workerContext.getWorkerResponse().setDataMessage(dataMessage);
    }
}
