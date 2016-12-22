package com.wolf.framework.service.parameter.request;

import com.wolf.framework.service.parameter.RequestDataType;
import com.wolf.framework.service.parameter.RequestParameterHandler;

/**
 * 邮箱类型
 *
 * @author aladdin
 */
public final class EmailRequestParameterHandlerImpl extends AbstractRegexRequestParameterHandler implements RequestParameterHandler {
    
    public EmailRequestParameterHandlerImpl(final String name) {
        super(name, RequestDataType.EMAIL, "^[a-z\\d]+[a-z\\d_]+@[a-z\\d]+\\.com$", " must be email");
    }

    @Override
    public String getDefaultValue() {
        return "";
    }
}
