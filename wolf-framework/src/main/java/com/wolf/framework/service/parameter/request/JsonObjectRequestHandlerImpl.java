package com.wolf.framework.service.parameter.request;

import com.wolf.framework.service.parameter.RequestDataType;
import com.wolf.framework.service.parameter.RequestHandler;
import java.util.Map;

/**
 * long类型处理类
 *
 * @author aladdin
 */
public final class JsonObjectRequestHandlerImpl implements RequestHandler {

    private final String name;
    private final boolean ignoreEmpty;
    private final String errorInfo = " must be json object";

    public JsonObjectRequestHandlerImpl(final String name, boolean ignoreEmpty) {
        this.name = name;
        this.ignoreEmpty = ignoreEmpty;
    }

    @Override
    public String validate(final Object value) {
        String msg = this.errorInfo;
        if (Map.class.isInstance(value)) {
            msg = "";
        }
        return msg;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public RequestDataType getDataType() {
        return RequestDataType.JSON_OBJECT;
    }

    @Override
    public boolean getIgnoreEmpty() {
        return ignoreEmpty;
    }

}
