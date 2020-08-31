package com.wolf.framework.service.parameter.response;

import com.wolf.framework.service.parameter.*;
import java.util.Map;

/**
 * long类型处理类
 *
 * @author aladdin
 */
public final class JsonObjectResponseHandlerImpl implements ResponseHandler {

    private final String name;

    public JsonObjectResponseHandlerImpl(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ResponseDataType getDataType() {
        return ResponseDataType.JSON_OBJECT;
    }

    @Override
    public Object getResponseValue(Object value) {
        if (Map.class.isInstance(value) == false) {
            String errMsg = "response:" + this.name + "'s type is not json object.";
            throw new RuntimeException(errMsg);
        }
        return value;
    }

}
