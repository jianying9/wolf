package com.wolf.framework.utils;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.service.parameter.ResponseDataType;
import com.wolf.framework.service.parameter.ResponseParameterHandler;
import com.wolf.framework.service.parameter.filter.Filter;
import com.wolf.framework.service.parameter.response.NumberResponseParameterHandlerImpl;
import com.wolf.framework.service.parameter.response.StringResponseParameterHandlerImpl;

/**
 *
 * @author jianying9
 */
public final class ResponseUtils {
    
    public static ResponseParameterHandler createStringResponseParameterHandler(String paraName) {
        Filter[] filters = ApplicationContext.CONTEXT.getFilterFactory().getAllFilter();
        return new StringResponseParameterHandlerImpl(paraName, filters);
    }
    
    public static ResponseParameterHandler createNumberResponseParameterHandler(String paraName) {
        return new NumberResponseParameterHandlerImpl(paraName, ResponseDataType.LONG);
    }
}
