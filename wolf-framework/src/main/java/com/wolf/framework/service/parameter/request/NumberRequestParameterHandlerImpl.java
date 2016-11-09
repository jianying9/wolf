package com.wolf.framework.service.parameter.request;

import com.wolf.framework.data.DataHandler;
import com.wolf.framework.service.parameter.RequestParameterHandler;

/**
 * 数字类型处理类
 *
 * @author aladdin
 */
public final class NumberRequestParameterHandlerImpl extends AbstractRequestParamperHandler implements RequestParameterHandler {

    private final long max;
    private final long min;

    public NumberRequestParameterHandlerImpl(final String name, final DataHandler dataHandler, final long max, final long min) {
        super(name, dataHandler);
        this.max = max > min ? max : min;
        this.min = max > min ? min : max;
    }

    private String gerErrorInfo() {
        StringBuilder err = new StringBuilder(64);
        err.append(this.dataHandler.getErrorInfo()).append('[')
                .append(this.min).append(',').append(this.max).append(']');
        return err.toString();
    }

    @Override
    public String validate(final String value) {
        String msg = "";
        boolean result = this.dataHandler.validate(value);
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
}
