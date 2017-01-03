package com.wolf.framework.service.parameter.response;

import com.wolf.framework.service.parameter.ResponseDataType;
import com.wolf.framework.service.parameter.ResponseParameterHandler;
import java.util.List;

/**
 * long array类型处理类
 *
 * @author aladdin
 */
public final class LongArrayResponseParameterHandlerImpl implements ResponseParameterHandler {

    private final String name;

    public LongArrayResponseParameterHandlerImpl(String name) {
        this.name = name;
    }
    
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ResponseDataType getDataType() {
        return ResponseDataType.LONG_ARRAY;
    }

    @Override
    public Object getResponseValue(Object value) {
        boolean isLongArray = true;
        if(List.class.isInstance(value) == false) {
            isLongArray = false;
        } else {
            List<Object> objectList = (List<Object>) value;
            for (Object object : objectList) {
                if(Long.class.isInstance(object) == false) {
                    isLongArray = false;
                    break;
                }
            }
        }
        if(isLongArray == false) {
            String errMsg = "response:" + this.name + "'s type is not List array.";
            throw new RuntimeException(errMsg);
        }
        return value;
    }
}
