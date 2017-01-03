package com.wolf.framework.service.parameter.request;

import com.wolf.framework.service.parameter.ObjectRequestHandlerInfo;
import com.wolf.framework.service.parameter.RequestDataType;
import com.wolf.framework.service.parameter.RequestParameterHandler;
import java.util.List;
import java.util.Map;

/**
 * object array类型处理类
 *
 * @author jianying9
 */
public final class ObjectArrayRequestParameterHandlerImpl extends AbstractObjectRequestParameterHandler implements RequestParameterHandler {

    private final String errorInfo = " must be object array";

    public ObjectArrayRequestParameterHandlerImpl(final String name, boolean ignoreEmpty, ObjectRequestHandlerInfo objectRequestHandlerInfo) {
        super(name, ignoreEmpty, objectRequestHandlerInfo);
    }

    @Override
    public String validate(final Object value) {
        String msg = this.errorInfo;
        if (List.class.isInstance(value)) {
            boolean isMap = true;
            Map<String, Object> valueMap;
            List<Object> objectList = (List<Object>) value;
            for (Object object : objectList) {
                if (Map.class.isInstance(object) == false) {
                    isMap = false;
                    break;
                } else {
                    valueMap = (Map<String, Object>) object;
                    //验证必填参数
                    msg = this.validateRequiredParameter(valueMap);
                    if (msg.isEmpty()) {
                        msg = this.validateUnrequiredParameter(valueMap);
                    } else {
                        break;
                    }
                }
            }
            if (isMap) {
                msg = "";
            }
        }
        return msg;
    }

    @Override
    public RequestDataType getDataType() {
        return RequestDataType.OBJECT_ARRAY;
    }

}
