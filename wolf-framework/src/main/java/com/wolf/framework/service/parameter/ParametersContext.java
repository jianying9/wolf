package com.wolf.framework.service.parameter;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.data.DataHandlerFactory;
import com.wolf.framework.service.parameter.filter.FilterFactory;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author aladdin
 */
public interface ParametersContext {

    public ParametersHandler getParametersHandler(final Class<?> clazz);

    public Map<Class<?>, ParametersHandler> getParametersHandlerMap();

    public void putParametersHandler(final Class<?> clazz, final ParametersHandler extendedEntityHandler);

    public boolean assertExistParametersConfig(final Class<?> clazz);

    public FilterFactory getFilterFactory();

    public DataHandlerFactory getDataHandlerFactory();

    public ApplicationContext getApplicationContext();
    
    public Set<String> getReservedWordSet();
}
