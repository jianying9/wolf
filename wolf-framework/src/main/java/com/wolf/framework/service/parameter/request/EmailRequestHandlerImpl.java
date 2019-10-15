package com.wolf.framework.service.parameter.request;

import com.wolf.framework.service.parameter.RequestDataType;
import com.wolf.framework.service.parameter.RequestHandler;

/**
 * 邮箱类型
 *
 * @author aladdin
 */
public final class EmailRequestHandlerImpl extends AbstractRegexRequestHandler implements RequestHandler {
    
    public EmailRequestHandlerImpl(final String name, boolean ignoreEmpty) {
        super(name, RequestDataType.EMAIL, "^[a-z\\d]+[a-z\\d_]+@[a-z\\d]+\\.com$", " must be email", ignoreEmpty);
    }

}
