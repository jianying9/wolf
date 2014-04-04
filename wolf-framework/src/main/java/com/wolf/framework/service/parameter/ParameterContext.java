package com.wolf.framework.service.parameter;

import com.wolf.framework.data.DataHandlerFactory;
import com.wolf.framework.service.parameter.filter.FilterFactory;
import java.util.Set;

/**
 *
 * @author aladdin
 */
public interface ParameterContext {

    public FilterFactory getFilterFactory();

    public DataHandlerFactory getDataHandlerFactory();

    public Set<String> getReservedWordSet();
}
