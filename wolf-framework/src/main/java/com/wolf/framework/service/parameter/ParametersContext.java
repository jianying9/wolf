package com.wolf.framework.service.parameter;

import java.util.Map;

/**
 *
 * @author aladdin
 */
public interface ParametersContext {

    public ParametersHandler getParametersHandler(final Class<?> clazz);

    public Map<Class<?>, ParametersHandler> getParametersHandlerMap();

    public void putParametersHandler(final Class<?> clazz, final ParametersHandler extendedEntityHandler);

    public boolean assertExistParametersConfig(final Class<?> clazz);

    public ParameterContextBuilder getFieldContextBuilder();
}
