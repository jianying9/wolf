package com.wolf.framework.service.parameter.request;

import com.wolf.framework.data.DataHandler;
import com.wolf.framework.service.parameter.RequestParameterHandler;

/**
 * 邮箱类型
 *
 * @author aladdin
 */
public final class EmailRequestParameterHandlerImpl extends AbstractRequestParamperHandler implements RequestParameterHandler {

    public EmailRequestParameterHandlerImpl(final String name, final DataHandler dataHandler) {
        super(name, dataHandler);
    }

    @Override
    public String validate(String value) {
        boolean result = this.dataHandler.validate(value);
        return result ? "" : this.dataHandler.getErrorInfo();
    }
}
