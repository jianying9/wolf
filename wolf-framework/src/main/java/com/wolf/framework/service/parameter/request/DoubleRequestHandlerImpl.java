package com.wolf.framework.service.parameter.request;

import com.wolf.framework.service.parameter.RequestDataType;
import com.wolf.framework.service.parameter.RequestHandler;

/**
 * double类型处理类
 *
 * @author aladdin
 */
public final class DoubleRequestHandlerImpl implements RequestHandler {

    private final long max;
    private final long min;
    private final String name;
    private final String errorInfo;
    private final boolean ignoreEmpty;

    public DoubleRequestHandlerImpl(final String name, final long max, final long min, boolean ignoreEmpty) {
        this.max = max > min ? max : min;
        this.min = max > min ? min : max;
        this.name = name;
        StringBuilder err = new StringBuilder(64);
        err.append(" must be double").append('[')
                .append(this.min).append(',').append(this.max).append(']');
        this.errorInfo = err.toString();
        this.ignoreEmpty = ignoreEmpty;
    }

    @Override
    public String validate(final Object value) {
        String msg = this.errorInfo;
        Double num = null;
        if (Long.class.isInstance(value)) {
            Long l = (Long) value;
            num = l.doubleValue();
        } else if (Double.class.isInstance(value)) {
            num = (Double) value;
        }
        if (num != null && num <= this.max && num >= this.min) {
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
        return RequestDataType.DOUBLE;
    }

    @Override
    public boolean getIgnoreEmpty() {
        return this.ignoreEmpty;
    }

}
