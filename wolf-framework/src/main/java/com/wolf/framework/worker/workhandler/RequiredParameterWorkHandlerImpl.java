package com.wolf.framework.worker.workhandler;

import com.wolf.framework.reponse.WorkerResponse;
import com.wolf.framework.request.WorkerRequest;
import com.wolf.framework.service.context.ServiceContext;
import com.wolf.framework.worker.context.WorkerContext;
import java.util.List;
import java.util.Map;
import com.wolf.framework.service.parameter.RequestHandler;

/**
 * 必要参数处理
 *
 * @author aladdin
 */
public class RequiredParameterWorkHandlerImpl implements WorkHandler {

    private final WorkHandler nextWorkHandler;
    private final ServiceContext serviceContext;

    public RequiredParameterWorkHandlerImpl(final WorkHandler workHandler, ServiceContext serviceContext) {
        this.nextWorkHandler = workHandler;
        this.serviceContext = serviceContext;
    }

    @Override
    public void execute(WorkerContext workerContext) {
        Object paraValue;
        String errorParaName = "";
        String errorMsg = "";
        String stringValue;
        List listValue;
        RequestHandler parameterHandler;
        final Map<String, Object> parameterMap = workerContext.getParameterMap();
        final WorkerRequest request = workerContext.getWorkerRequest();
        //验证必要参数是否合法
        final String[] requiredParameter = this.serviceContext.requiredParameter();
        final Map<String, RequestHandler> parameterHandlerMap = this.serviceContext.requestParameterHandlerMap();
        for (String parameter : requiredParameter) {
            paraValue = parameterMap.get(parameter);
            if (paraValue == null) {
                errorMsg = " is null";
                errorParaName = parameter;
                break;
            }
            if (String.class.isInstance(paraValue)) {
                stringValue = (String) paraValue;
                if (stringValue.isEmpty()) {
                    errorMsg = " is empty";
                    errorParaName = parameter;
                    break;
                }
            }
            if (List.class.isInstance(paraValue)) {
                listValue = (List) paraValue;
                if (listValue.isEmpty()) {
                    errorMsg = " is empty";
                    errorParaName = parameter;
                    break;
                }
            }
            parameterHandler = parameterHandlerMap.get(parameter);
            errorMsg = parameterHandler.validate(paraValue);
            if (errorMsg.isEmpty()) {
                request.putParameter(parameter, paraValue);
            } else {
                errorParaName = parameter;
                break;
            }
        }
        if (errorMsg.isEmpty()) {
            //验证通过
            this.nextWorkHandler.execute(workerContext);
        } else {
            //返回错误消息
            WorkerResponse response = workerContext.getWorkerResponse();
            errorMsg = errorParaName.concat(errorMsg);
            response.invalid();
            response.setError(errorMsg);
        }
    }
}
