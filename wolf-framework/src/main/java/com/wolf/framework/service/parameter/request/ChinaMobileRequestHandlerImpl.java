package com.wolf.framework.service.parameter.request;

import com.wolf.framework.service.parameter.RequestDataType;
import com.wolf.framework.service.parameter.RequestHandler;

/**
 * 中国手机号码类型处理类
 *
 * @author aladdin
 */
public final class ChinaMobileRequestHandlerImpl extends AbstractRegexRequestHandler implements RequestHandler {
    
    public ChinaMobileRequestHandlerImpl(final String name, boolean ignoreEmpty) {
        super(name, RequestDataType.CHINA_MOBILE, "^1[3578]{1}\\d{9}\\d?$", " must be china mobile", ignoreEmpty);
    }
}
