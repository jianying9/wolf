package com.wolf.framework.service.parameter.request;

import com.wolf.framework.data.DataHandler;
import com.wolf.framework.service.parameter.RequestParameterHandler;

/**
 * 时间类型处理类
 *
 * @author aladdin
 */
public final class DateRequestParameterHandlerImpl extends AbstractRequestParamperHandler implements RequestParameterHandler {

    public DateRequestParameterHandlerImpl(final String name, final DataHandler dataHandler) {
        super(name, dataHandler);
    }

    @Override
    public String validate(final String value) {
        boolean result = this.dataHandler.validate(value);
        return result ? "" : this.dataHandler.getErrorInfo();
    }
}
