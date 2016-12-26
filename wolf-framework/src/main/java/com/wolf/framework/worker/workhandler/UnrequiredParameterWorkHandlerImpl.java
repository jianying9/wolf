package com.wolf.framework.worker.workhandler;

import com.wolf.framework.reponse.WorkerResponse;
import com.wolf.framework.request.WorkerRequest;
import com.wolf.framework.service.context.ServiceContext;
import com.wolf.framework.service.parameter.RequestParameterHandler;
import com.wolf.framework.worker.context.WorkerContext;
import java.util.Map;

/**
 * 次要参数处理类,保留空字符
 *
 * @author aladdin
 */
public class UnrequiredParameterWorkHandlerImpl implements WorkHandler {

    private final WorkHandler nextWorkHandler;
    private final ServiceContext serviceContext;

    public UnrequiredParameterWorkHandlerImpl(final WorkHandler workHandler, ServiceContext serviceContext) {
        this.serviceContext = serviceContext;
        this.nextWorkHandler = workHandler;

    }

    @Override
    public void execute(WorkerContext workerContext) {
        Object paraValue;
        String value;
        String errorParaName = "";
        String errorMsg = "";
        RequestParameterHandler parameterHandler;
        //验证必要参数是否合法
        final Map<String, Object> parameterMap = workerContext.getParameterMap();
        final WorkerRequest request = workerContext.getWorkerRequest();
        final String[] unrequiredParameter = this.serviceContext.unrequiredParameter();
        final Map<String, RequestParameterHandler> parameterHandlerMap = this.serviceContext.requestParameterHandlerMap();
        for (String parameter : unrequiredParameter) {
            paraValue = parameterMap.get(parameter);
            if (paraValue != null) {
                //非空验证
                parameterHandler = parameterHandlerMap.get(parameter);
                if (String.class.isInstance(paraValue)) {
                    value = (String) paraValue;
                    if (value.isEmpty() == false || parameterHandler.getIgnoreEmpty() == false) {
                        //如果输入参数不为空字符或则该参数不允许忽略空字符串,那么必须进行类型验证
                        errorMsg = parameterHandler.validate(paraValue);
                        if (errorMsg.isEmpty()) {
                            request.putParameter(parameter, paraValue);
                        } else {
                            errorParaName = parameter;
                            break;
                        }
                    }
                } else {
                    errorMsg = parameterHandler.validate(paraValue);
                    if (errorMsg.isEmpty()) {
                        request.putParameter(parameter, paraValue);
                    } else {
                        errorParaName = parameter;
                        break;
                    }
                }
            }
            if (errorMsg.isEmpty()) {
                //验证通过
                this.nextWorkHandler.execute(workerContext);
            } else {
                errorMsg = errorParaName.concat(errorMsg);
                WorkerResponse response = workerContext.getWorkerResponse();
                response.invalid();
                response.setError(errorMsg);
            }
        }
    }
}
