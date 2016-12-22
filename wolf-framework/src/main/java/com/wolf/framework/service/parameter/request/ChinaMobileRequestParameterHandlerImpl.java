package com.wolf.framework.service.parameter.request;

import com.wolf.framework.service.parameter.RequestDataType;
import com.wolf.framework.service.parameter.RequestParameterHandler;

/**
 * 中国手机号码类型处理类
 *
 * @author aladdin
 */
public final class ChinaMobileRequestParameterHandlerImpl extends AbstractRegexRequestParameterHandler implements RequestParameterHandler {
    
    public ChinaMobileRequestParameterHandlerImpl(final String name) {
        super(name, RequestDataType.CHINA_MOBILE, "^1[3578]{1}\\d{9}\\d?$", " must be china mobile");
    }

    @Override
    public String getDefaultValue() {
        return "";
    }
}
