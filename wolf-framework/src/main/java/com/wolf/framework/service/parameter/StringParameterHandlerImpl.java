package com.wolf.framework.service.parameter;

import com.wolf.framework.data.DataType;
import com.wolf.framework.service.parameter.filter.Filter;

/**
 * 字符类型处理类
 *
 * @author aladdin
 */
public final class StringParameterHandlerImpl implements RequestParameterHandler, ResponseParameterHandler {

    private final Filter[] filters;
    private final long max;
    private final long min;
    private final String name;
    private final String errorInfo = " must be char";
    

    public StringParameterHandlerImpl(final String name, final Filter[] filters, long max, long min) {
        this.filters = filters;
        this.name = name;
        max = max < 0 ? 0 : max;
        min = min < 0 ? 0 : min;
        this.max = max > min ? max : min;
        this.min = max > min ? min : max;
    }
    
    private String gerErrorInfo() {
        StringBuilder err = new StringBuilder(64);
        err.append(this.errorInfo).append('[')
                .append(this.min).append(',').append(this.max).append(']');
        return err.toString();
    }
    
    @Override
    public String validate(String value) {
        String msg = "";
        if(value.length() > this.max | value.length() < this.min) {
            msg = this.gerErrorInfo();
        }
        return msg;
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

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public DataType getDataType() {
        return DataType.STRING;
    }
}
