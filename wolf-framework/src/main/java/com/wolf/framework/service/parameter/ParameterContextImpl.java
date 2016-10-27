package com.wolf.framework.service.parameter;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.data.DataHandlerFactory;
import com.wolf.framework.service.parameter.filter.FilterFactory;
import com.wolf.framework.service.parameter.filter.FilterFactoryImpl;

/**
 * 全局信息构造类
 *
 * @author aladdin
 */
public class ParameterContextImpl implements ParameterContext {

    //extended entity处理集合
    private final FilterFactory filterFactory;
    private final DataHandlerFactory dataHandlerFactory;

    /**
     * 构造函数
     *
     * @param dataHandlerFactory
     * @param applicationContext
     */
    public ParameterContextImpl(final DataHandlerFactory dataHandlerFactory, final ApplicationContext applicationContext) {
        this.filterFactory = new FilterFactoryImpl();
        this.dataHandlerFactory = dataHandlerFactory;
    }

    @Override
    public final FilterFactory getFilterFactory() {
        return this.filterFactory;
    }

    @Override
    public final DataHandlerFactory getDataHandlerFactory() {
        return this.dataHandlerFactory;
    }
}
