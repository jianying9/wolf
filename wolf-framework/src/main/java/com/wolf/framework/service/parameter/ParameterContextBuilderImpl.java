package com.wolf.framework.service.parameter;

import com.wolf.framework.data.DataHandlerFactory;
import com.wolf.framework.service.parameter.filter.FilterFactory;
import com.wolf.framework.service.parameter.filter.FilterFactoryImpl;

/**
 * 全局信息构造类
 *
 * @author aladdin
 */
public class ParameterContextBuilderImpl implements ParameterContextBuilder {

    private final FilterFactory filterFactory;

    @Override
    public final FilterFactory getFilterFactory() {
        return this.filterFactory;
    }
    private final DataHandlerFactory dataHandlerFactory;

    @Override
    public final DataHandlerFactory getDataHandlerFactory() {
        return this.dataHandlerFactory;
    }

    /**
     * 构造函数
     *
     * @param properties
     */
    public ParameterContextBuilderImpl(final DataHandlerFactory dataHandlerFactory) {
        this.filterFactory = new FilterFactoryImpl();
        this.dataHandlerFactory = dataHandlerFactory;
    }
}
