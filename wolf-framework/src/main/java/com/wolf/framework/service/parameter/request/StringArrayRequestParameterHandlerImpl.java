package com.wolf.framework.service.parameter.request;

import com.wolf.framework.service.parameter.RequestDataType;
import com.wolf.framework.service.parameter.RequestParameterHandler;
import java.util.List;

/**
 * string array类型处理类
 *
 * @author jianying9
 */
public final class StringArrayRequestParameterHandlerImpl implements RequestParameterHandler {

    private final String name;
    private final String errorInfo = " must be string array";
    private final boolean ignoreEmpty;

    public StringArrayRequestParameterHandlerImpl(final String name, boolean ignoreEmpty) {
        this.name = name;
        this.ignoreEmpty = ignoreEmpty;
    }

    @Override
    public String validate(final Object value) {
        String msg = this.errorInfo;
        if (List.class.isInstance(value)) {
            boolean isString = true;
            List<Object> objectList = (List<Object>) value;
            for (Object object : objectList) {
                if(String.class.isInstance(object) == false) {
                    isString = false;
                    break;
                }
            }
            if(isString) {
                msg = "";
            }
        }
        return msg;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public RequestDataType getDataType() {
        return RequestDataType.STRING_ARRAY;
    }

    @Override
    public boolean getIgnoreEmpty() {
        return ignoreEmpty;
    }

}
