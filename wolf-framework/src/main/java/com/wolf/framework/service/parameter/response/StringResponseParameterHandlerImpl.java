package com.wolf.framework.service.parameter.response;

import com.wolf.framework.data.DataType;
import com.wolf.framework.service.parameter.ResponseParameterHandler;
import com.wolf.framework.service.parameter.filter.Filter;

/**
 * 字符类型处理类
 *
 * @author aladdin
 */
public final class StringResponseParameterHandlerImpl extends AbstractResponseParameterHandler implements ResponseParameterHandler {

    private final Filter[] filters;

    public StringResponseParameterHandlerImpl(final String name, final Filter[] filters) {
        super(name, DataType.STRING);
        this.filters = filters;
    }
    
    @Override
    public String getJson(final String value) {
        String result;
        String filterValue = value;
        if (this.filters != null) {
            for (Filter filter : filters) {
                filterValue = filter.doFilter(filterValue);
            }
        }
        StringBuilder jsonBuilder = new StringBuilder(this.name.length() + filterValue.length() + 5);
        jsonBuilder.append('"').append(this.name).append("\":\"").append(filterValue).append('"');
        result = jsonBuilder.toString();
        return result;
    }
}
