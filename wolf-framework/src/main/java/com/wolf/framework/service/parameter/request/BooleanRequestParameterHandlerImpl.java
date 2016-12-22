package com.wolf.framework.service.parameter.request;

import com.wolf.framework.service.parameter.RequestDataType;
import com.wolf.framework.service.parameter.RequestParameterHandler;

/**
 * boolean类型处理类
 *
 * @author aladdin
 */
public final class BooleanRequestParameterHandlerImpl extends AbstractRegexRequestParameterHandler implements RequestParameterHandler {
    
    public BooleanRequestParameterHandlerImpl(final String name, boolean ignoreEmpty) {
        super(name, RequestDataType.BOOLEAN, "^true|false$", " must be boolean", ignoreEmpty);
    }

}
