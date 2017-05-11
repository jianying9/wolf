package com.wolf.framework.service.parameter.request;

import com.wolf.framework.service.parameter.ObjectRequestHandlerInfo;
import com.wolf.framework.service.parameter.RequestDataType;
import java.util.List;
import java.util.Map;
import com.wolf.framework.service.parameter.RequestHandler;

/**
 * object array类型处理类
 *
 * @author jianying9
 */
public final class ObjectArrayRequestHandlerImpl extends AbstractObjectRequestHandler implements RequestHandler {

    private final String errorInfo = " must be object array";

    public ObjectArrayRequestHandlerImpl(final String name, boolean ignoreEmpty, ObjectRequestHandlerInfo objectRequestHandlerInfo) {
        super(name, ignoreEmpty, objectRequestHandlerInfo);
    }

    @Override
    public String validate(final Object value) {
        String msg = this.errorInfo;
        if (List.class.isInstance(value)) {
            Map<String, Object> valueMap;
            List<Object> objectList = (List<Object>) value;
            for (Object object : objectList) {
                if (Map.class.isInstance(object) == false) {
                    msg = this.errorInfo;
                    break;
                } else {
                    valueMap = (Map<String, Object>) object;
                    //验证必填参数
                    msg = this.validateRequiredParameter(valueMap);
                    if (msg.isEmpty()) {
                        //验证选填参数
                        msg = this.validateUnrequiredParameter(valueMap);
                        if(msg.isEmpty() == false) {
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
        }
        return msg;
    }

    @Override
    public RequestDataType getDataType() {
        return RequestDataType.OBJECT_ARRAY;
    }

}
