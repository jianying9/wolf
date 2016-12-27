package com.wolf.framework.service.parameter.request;

import com.wolf.framework.service.parameter.RequestDataType;
import com.wolf.framework.service.parameter.RequestParameterHandler;
import java.util.List;

/**
 * long array类型处理类
 *
 * @author jianying9
 */
public final class LongArrayRequestParameterHandlerImpl implements RequestParameterHandler {

    private final String name;
    private final String errorInfo = " must be long array";
    private final boolean ignoreEmpty;

    public LongArrayRequestParameterHandlerImpl(final String name, boolean ignoreEmpty) {
        this.name = name;
        this.ignoreEmpty = ignoreEmpty;
    }

    @Override
    public String validate(final Object value) {
        String msg = this.errorInfo;
        if (List.class.isInstance(value)) {
            boolean isLong = true;
            List<Object> objectList = (List<Object>) value;
            for (Object object : objectList) {
                if(Long.class.isInstance(object) == false) {
                    isLong = false;
                    break;
                }
            }
            if(isLong) {
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
        return RequestDataType.LONG;
    }

    @Override
    public boolean getIgnoreEmpty() {
        return ignoreEmpty;
    }

}
