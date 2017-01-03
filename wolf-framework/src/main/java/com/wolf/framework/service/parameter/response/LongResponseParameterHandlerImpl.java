package com.wolf.framework.service.parameter.response;

import com.wolf.framework.service.parameter.*;

/**
 * long类型处理类
 *
 * @author aladdin
 */
public final class LongResponseParameterHandlerImpl implements ResponseParameterHandler {

    private final String name;

    public LongResponseParameterHandlerImpl(String name) {
        this.name = name;
    }
    

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ResponseDataType getDataType() {
        return ResponseDataType.LONG;
    }

    @Override
    public Object getResponseValue(Object value) {
        if(Long.class.isInstance(value) == false) {
            String errMsg = "response:" + this.name + "'s type is not Long.";
            throw new RuntimeException(errMsg);
        }
        return value;
    }
    
}
