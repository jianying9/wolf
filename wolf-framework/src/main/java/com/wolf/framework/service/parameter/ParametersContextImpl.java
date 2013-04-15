package com.wolf.framework.service.parameter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局信息构造类
 *
 * @author aladdin
 */
public class ParametersContextImpl implements ParametersContext {

    //extended entity处理集合
    private final Map<Class<?>, ParametersHandler> parametersHandlerMap;
    private final ParameterContextBuilder fieldContextBuilder;

    /**
     * 构造函数
     *
     * @param properties
     */
    public ParametersContextImpl(final ParameterContextBuilder fieldContextBuilder) {
        this.fieldContextBuilder = fieldContextBuilder;
        this.parametersHandlerMap = new HashMap<Class<?>, ParametersHandler>(16, 1);
    }

    @Override
    public final void putParametersHandler(final Class<?> clazz, final ParametersHandler parametersHandler) {
        if (!this.parametersHandlerMap.containsKey(clazz)) {
            this.parametersHandlerMap.put(clazz, parametersHandler);
        }
    }

    @Override
    public final ParametersHandler getParametersHandler(final Class<?> clazz) {
        return this.parametersHandlerMap.get(clazz);
    }

    @Override
    public final Map<Class<?>, ParametersHandler> getParametersHandlerMap() {
        return Collections.unmodifiableMap(this.parametersHandlerMap);
    }

    @Override
    public boolean assertExistParametersConfig(final Class<?> clazz) {
        return this.parametersHandlerMap.containsKey(clazz);
    }

    @Override
    public ParameterContextBuilder getFieldContextBuilder() {
        return this.fieldContextBuilder;
    }
}
