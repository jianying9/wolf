package com.wolf.framework.service.parameter;

import com.wolf.framework.data.DataHandlerFactory;
import com.wolf.framework.service.parameter.filter.FilterFactory;

/**
 *
 * @author aladdin
 */
public interface ParameterContextBuilder {

    public FilterFactory getFilterFactory();

    public DataHandlerFactory getDataHandlerFactory();
}
