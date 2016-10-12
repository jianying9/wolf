package com.wolf.framework.worker.context;

import com.wolf.framework.service.parameter.ResponseParameterHandler;
import com.wolf.framework.utils.JsonUtils;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public final class MessageContextImpl extends AbstractMessageContext {

    private Map<String, String> mapData;

    public MessageContextImpl(WorkerContext workerContext, String[] returnParameter, Map<String, ResponseParameterHandler> parameterHandlerMap) {
        super(workerContext, returnParameter, parameterHandlerMap);
    }

    @Override
    public String createMessage() {
        StringBuilder jsonBuilder = new StringBuilder(64);
        String data = "{}";
        if (this.returnParameter.length > 0 && this.mapData != null) {
            data = JsonUtils.mapToJSON(this.mapData, this.returnParameter, this.parameterHandlerMap);
        }
        jsonBuilder.append("{\"state\":\"").append(this.state)
                .append("\",\"route\":\"").append(this.workerContext.getRoute())
                .append("\",\"data\":").append(data);
        if (this.newSid != null) {
            jsonBuilder.append(",\"sid\":\"").append(this.newSid).append('"');
        }
        jsonBuilder.append('}');
        return jsonBuilder.toString();
    }

    @Override
    public void setMapData(Map<String, String> parameterMap) {
        this.mapData = parameterMap;
    }

    @Override
    public Map<String, String> getMapData() {
        return this.mapData;
    }
}
