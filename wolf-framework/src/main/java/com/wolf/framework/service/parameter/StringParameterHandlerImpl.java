package com.wolf.framework.service.parameter;

import com.wolf.framework.data.DataHandler;
import com.wolf.framework.service.parameter.filter.Filter;

/**
 * 字符类型处理类
 *
 * @author aladdin
 */
public final class StringParameterHandlerImpl extends AbstractParameterHandler implements ParameterHandler {

    private final Filter[] filters;

    StringParameterHandlerImpl(final String name, final Filter[] filters, final DataHandler dataHandler, final String defaultValue, final String desc) {
        super(name, dataHandler, defaultValue, desc);
        this.filters = filters;
    }

    @Override
    public String getJson(final String value) {
        String result;
        String filterValue = value;
        for (Filter filter : filters) {
            filterValue = filter.doFilter(filterValue);
        }
        StringBuilder jsonBuilder = new StringBuilder(this.name.length() + filterValue.length() + 5);
        jsonBuilder.append('"').append(this.name).append("\":\"").append(filterValue).append('"');
        result = jsonBuilder.toString();
        return result;
    }
}
