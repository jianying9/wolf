package com.wolf.framework.service.parameter;

import com.wolf.framework.config.FrameworkConfig;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.data.DataHandlerFactory;
import com.wolf.framework.service.parameter.filter.FilterFactory;
import com.wolf.framework.service.parameter.filter.FilterFactoryImpl;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 全局信息构造类
 *
 * @author aladdin
 */
public class ParametersContextImpl implements ParametersContext {

    //extended entity处理集合
    private final Map<Class<?>, ParametersHandler> parametersHandlerMap;
    private final FilterFactory filterFactory;
    private final DataHandlerFactory dataHandlerFactory;
    private final ApplicationContext applicationContext;
    private final Set<String> reservedWordSet;

    /**
     * 构造函数
     *
     * @param properties
     */
    public ParametersContextImpl(final DataHandlerFactory dataHandlerFactory, final ApplicationContext applicationContext) {
        this.filterFactory = new FilterFactoryImpl();
        this.parametersHandlerMap = new HashMap<Class<?>, ParametersHandler>(2, 1);
        this.dataHandlerFactory = dataHandlerFactory;
        this.reservedWordSet = FrameworkConfig.getReservedWordSet();
        this.applicationContext = applicationContext;
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
    public final FilterFactory getFilterFactory() {
        return this.filterFactory;
    }

    @Override
    public final DataHandlerFactory getDataHandlerFactory() {
        return this.dataHandlerFactory;
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    @Override
    public Set<String> getReservedWordSet() {
        return this.reservedWordSet;
    }
}
