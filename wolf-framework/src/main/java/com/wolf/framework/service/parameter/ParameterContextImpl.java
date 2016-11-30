package com.wolf.framework.service.parameter;

import com.wolf.framework.context.ApplicationContext;
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

    /**
     * 构造函数
     *
     * @param applicationContext
     */
    public ParameterContextImpl(final ApplicationContext applicationContext) {
        this.filterFactory = new FilterFactoryImpl();
    }

    @Override
    public final FilterFactory getFilterFactory() {
        return this.filterFactory;
    }

}
