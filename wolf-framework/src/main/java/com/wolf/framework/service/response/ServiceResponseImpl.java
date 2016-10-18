package com.wolf.framework.service.response;

import com.wolf.framework.dao.Entity;
import com.wolf.framework.reponse.Response;
import com.wolf.framework.service.parameter.ResponseParameterHandler;
import com.wolf.framework.utils.JsonUtils;
import java.util.Map;

/**
 *
 * @author jianying9
 * @param <T>
 */
public class ServiceResponseImpl<T extends Entity> extends AbstractServiceResponse implements ServiceResponse<T> {
    
    private final String[] returnParameter;
    private final Map<String, ResponseParameterHandler> parameterHandlerMap;
    private Map<String, String> dataMap = null;

    public ServiceResponseImpl(Response response, String[] returnParameter, Map<String, ResponseParameterHandler> parameterHandlerMap) {
        super(response);
        this.returnParameter = returnParameter;
        this.parameterHandlerMap = parameterHandlerMap;
    }

    @Override
    public void setEntity(Entity t) {
        this.setDataMap(t.toMap());
    }

    @Override
    public void setDataMap(Map<String, String> dataMap) {
        this.dataMap = dataMap;
    }

    @Override
    public String getDataMessage() {
        String dataMessage = JsonUtils.mapToJSON(this.dataMap, this.returnParameter, this.parameterHandlerMap);
        return dataMessage;
    }
}
