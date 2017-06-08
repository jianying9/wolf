package com.wolf.framework.service.parameter.request;

import com.wolf.framework.service.parameter.RequestDataType;
import com.wolf.framework.service.parameter.RequestHandler;

/**
 * long类型处理类
 *
 * @author aladdin
 */
public final class LongRequestHandlerImpl implements RequestHandler {

    private final long max;
    private final long min;
    private final String name;
    private final String errorInfo;
    private final boolean ignoreEmpty;

    public LongRequestHandlerImpl(final String name, final long max, final long min, boolean ignoreEmpty) {
        this.max = max > min ? max : min;
        this.min = max > min ? min : max;
        this.name = name;
        StringBuilder err = new StringBuilder(64);
        err.append(" must be long").append('[')
                .append(this.min).append(',').append(this.max).append(']');
        this.errorInfo = err.toString();
        this.ignoreEmpty = ignoreEmpty;
    }

    @Override
    public String validate(final Object value) {
        String msg = this.errorInfo;
        Long num = null;
        if (Long.class.isInstance(value)) {
            num = (Long) value;
        } else if (Integer.class.isInstance(value)) {
                Integer i = (Integer) value;
                num = i.longValue();
        } else if (String.class.isInstance(value)) {
            String v = (String) value;
            try {
                num = Long.valueOf(v);
            } catch (NumberFormatException e) {
            }
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
        return RequestDataType.LONG;
    }

    @Override
    public boolean getIgnoreEmpty() {
        return ignoreEmpty;
    }

}
