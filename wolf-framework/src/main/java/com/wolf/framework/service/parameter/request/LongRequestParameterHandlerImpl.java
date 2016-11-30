package com.wolf.framework.service.parameter.request;

import com.wolf.framework.service.parameter.RequestDataType;
import com.wolf.framework.service.parameter.RequestParameterHandler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * long类型处理类
 *
 * @author aladdin
 */
public final class LongRequestParameterHandlerImpl implements RequestParameterHandler {

    private final long max;
    private final long min;
    private final String name;
    private final String errorInfo;
    private final Pattern pattern = Pattern.compile("^\\d|[1-9]\\d{1,17}|-[1-9]\\d{0,17}$");

    public LongRequestParameterHandlerImpl(final String name, final long max, final long min) {
        this.max = max > min ? max : min;
        this.min = max > min ? min : max;
        this.name = name;
        StringBuilder err = new StringBuilder(64);
        err.append(" must be long").append('[')
                .append(this.min).append(',').append(this.max).append(']');
        this.errorInfo = err.toString();
    }

    private String gerErrorInfo() {
        return this.errorInfo;
    }

    @Override
    public String validate(final String value) {
        String msg = "";
        Matcher matcher = this.pattern.matcher(value);
        boolean result = matcher.matches();
        if (result) {
            long num = Long.parseLong(value);
            if (num > this.max || num < this.min) {
                msg = this.gerErrorInfo();
            }
        } else {
            msg = this.gerErrorInfo();
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
}
