package com.wolf.framework.service.parameter.request;

import com.wolf.framework.service.parameter.RequestDataType;
import com.wolf.framework.service.parameter.RequestParameterHandler;
import java.util.List;
import java.util.Map;

/**
 * object array类型处理类
 *
 * @author jianying9
 */
public final class ObjectArrayRequestParameterHandlerImpl implements RequestParameterHandler {

    private final String name;
    private final String errorInfo = " must be object array";
    private final boolean ignoreEmpty;

    public ObjectArrayRequestParameterHandlerImpl(final String name, boolean ignoreEmpty) {
        this.name = name;
        this.ignoreEmpty = ignoreEmpty;
    }

    @Override
    public String validate(final Object value) {
        String msg = this.errorInfo;
        if (List.class.isInstance(value)) {
            boolean isMap = true;
            List<Object> objectList = (List<Object>) value;
            for (Object object : objectList) {
                if(Map.class.isInstance(object) == false) {
                    isMap = false;
                    break;
                }
            }
            if(isMap) {
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
        return RequestDataType.OBJECT_ARRAY;
    }

    @Override
    public boolean getIgnoreEmpty() {
        return ignoreEmpty;
    }

}
