package com.wolf.framework.service.parameter.request;

import com.wolf.framework.service.parameter.RequestDataType;
import com.wolf.framework.service.parameter.RequestParameterHandler;

/**
 * boolean类型处理类
 *
 * @author jianying9
 */
public final class BooleanRequestParameterHandlerImpl implements RequestParameterHandler {
    
    private final String name;
    private final boolean ignoreEmpty;
    private final String errorInfo = " must be boolean";
    
    public BooleanRequestParameterHandlerImpl(final String name, boolean ignoreEmpty) {
        this.name = name;
        this.ignoreEmpty = ignoreEmpty;
    }

    @Override
    public String validate(Object value) {
        String result = this.errorInfo;
        if(Boolean.class.isInstance(value)) {
            result = "";
        }
        return result;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public RequestDataType getDataType() {
        return RequestDataType.BOOLEAN;
    }

    @Override
    public boolean getIgnoreEmpty() {
        return this.ignoreEmpty;
    }

}
