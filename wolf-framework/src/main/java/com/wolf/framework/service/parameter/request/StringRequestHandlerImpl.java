package com.wolf.framework.service.parameter.request;

import com.wolf.framework.service.parameter.RequestDataType;
import com.wolf.framework.service.parameter.RequestHandler;

/**
 * 字符类型处理类
 *
 * @author aladdin
 */
public final class StringRequestHandlerImpl implements RequestHandler {

    private final long max;
    private final long min;
    private final String name;
    private final String errorInfo;
    private final boolean ignoreEmpty;

    public StringRequestHandlerImpl(final String name, long max, long min, boolean ignoreEmpty) {
        this.name = name;
        max = max < 0 ? 0 : max;
        min = min < 0 ? 0 : min;
        this.max = max > min ? max : min;
        this.min = max > min ? min : max;
        this.ignoreEmpty = ignoreEmpty;
        StringBuilder err = new StringBuilder(64);
        err.append(" must be string").append('[')
                .append(this.min).append(',').append(this.max).append(']');
        this.errorInfo = err.toString();
    }

    @Override
    public String validate(Object value) {
        String msg = this.errorInfo;
        String v = "";
        if (String.class.isInstance(value)) {
            v = (String) value;
        } else if (Integer.class.isInstance(value)) {
            Integer i = (Integer) value;
            v = i.toString();
        } else if (Long.class.isInstance(value)) {
            Long l = (Long) value;
            v = l.toString();
        } else if (Boolean.class.isInstance(value)) {
            Boolean b = (Boolean) value;
            v = b.toString();
        } else if (Double.class.isInstance(value)) {
            Double d = (Double) value;
            v = d.toString();
        }
        if (v.length() <= this.max || v.length() >= this.min) {
            msg = "";
        }
        return msg;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public RequestDataType getDataType() {
        return RequestDataType.STRING;
    }

    @Override
    public boolean getIgnoreEmpty() {
        return ignoreEmpty;
    }

}
