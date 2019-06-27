package com.wolf.framework.service.parameter.response;

import com.wolf.framework.service.parameter.*;
import java.util.List;

/**
 *
 *
 * @author aladdin
 */
public final class JsonArrayResponseHandlerImpl implements ResponseHandler {

    private final String name;

    public JsonArrayResponseHandlerImpl(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ResponseDataType getDataType() {
        return ResponseDataType.JSON_ARRAY;
    }

    @Override
    public Object getResponseValue(Object value) {
        if (List.class.isInstance(value) == false) {
            String errMsg = "response:" + this.name + "'s type is not json array.";
            throw new RuntimeException(errMsg);
        }
        return value;
    }

}
