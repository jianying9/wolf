package com.wolf.framework.service.parameter.request;

import com.wolf.framework.data.DataHandler;
import com.wolf.framework.service.parameter.RequestParameterHandler;

/**
 * 中国手机号码类型处理类
 *
 * @author aladdin
 */
public final class ChinaMobileRequestParameterHandlerImpl extends AbstractRequestParamperHandler implements RequestParameterHandler {

    public ChinaMobileRequestParameterHandlerImpl(final String name, final DataHandler dataHandler) {
        super(name, dataHandler);
    }

    @Override
    public String validate(String value) {
        boolean result = this.dataHandler.validate(value);
        return result ? "" : this.dataHandler.getErrorInfo();
    }
}
