package com.wolf.framework.worker.workhandler;

import com.wolf.framework.reponse.WorkerResponse;
import com.wolf.framework.request.WorkerRequest;
import com.wolf.framework.service.parameter.RequestParameterHandler;
import com.wolf.framework.worker.context.WorkerContext;
import java.util.Map;

/**
 * 必要参数处理
 *
 * @author aladdin
 */
public class ImportantParameterWorkHandlerImpl implements WorkHandler {

    private final WorkHandler nextWorkHandler;
    private final String[] importantParameter;
    private final Map<String, RequestParameterHandler> parameterHandlerMap;

    public ImportantParameterWorkHandlerImpl(
            final String[] importantParameter,
            final Map<String, RequestParameterHandler> parameterHandlerMap,
            final WorkHandler workHandler) {
        this.nextWorkHandler = workHandler;
        this.importantParameter = importantParameter;
        this.parameterHandlerMap = parameterHandlerMap;
    }

    @Override
    public void execute(WorkerContext workerContext) {
        String paraValue;
        String errorParaName = "";
        String errorMsg = "";
        RequestParameterHandler parameterHandler;
        final Map<String, String> parameterMap = workerContext.getParameterMap();
        final WorkerRequest request = workerContext.getWorkerRequest();
        //验证必要参数是否合法
        for (String parameter : this.importantParameter) {
            paraValue = parameterMap.get(parameter);
            if (paraValue == null) {
                errorMsg = WorkHandler.NULL_MESSAGE;
                errorParaName = parameter;
                break;
            }
            if (paraValue.isEmpty()) {
                errorMsg = WorkHandler.EMPTY_MESSAGE;
                errorParaName = parameter;
                break;
            }
            parameterHandler = this.parameterHandlerMap.get(parameter);
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
            response.createErrorMessage();
        }
    }
}
