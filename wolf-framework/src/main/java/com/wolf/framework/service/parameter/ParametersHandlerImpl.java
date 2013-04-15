package com.wolf.framework.service.parameter;

import java.util.Collections;
import java.util.Map;

/**
 * 实体处理类
 *
 * @author aladdin
 */
public final class ParametersHandlerImpl implements ParametersHandler {

    private final Map<String, ParameterHandler> parameterHandlerMap;

    ParametersHandlerImpl(final Map<String, ParameterHandler> parameterHandlerMap) {
        this.parameterHandlerMap = parameterHandlerMap;
    }

    @Override
    public ParameterHandler getFieldHandler(final String fieldName) {
        return this.parameterHandlerMap.get(fieldName);
    }

    @Override
    public boolean containsField(String fieldName) {
        return this.parameterHandlerMap.containsKey(fieldName);
    }

    @Override
    public Map<String, ParameterHandler> getFieldHandlerMap() {
        return Collections.unmodifiableMap(this.parameterHandlerMap);
    }
}
