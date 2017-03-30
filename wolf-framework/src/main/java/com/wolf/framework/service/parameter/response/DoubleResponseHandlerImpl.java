package com.wolf.framework.service.parameter.response;

import com.wolf.framework.service.parameter.*;

/**
 * double类型处理类
 *
 * @author aladdin
 */
public final class DoubleResponseHandlerImpl implements ResponseHandler {

    private final String name;

    public DoubleResponseHandlerImpl(String name) {
        this.name = name;
    }
    

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ResponseDataType getDataType() {
        return ResponseDataType.DOUBLE;
    }

    @Override
    public Object getResponseValue(Object value) {
        if(Double.class.isInstance(value) == false) {
            String errMsg = "response:" + this.name + "'s type is not Double.";
            throw new RuntimeException(errMsg);
        }
        return value;
    }
    
}
