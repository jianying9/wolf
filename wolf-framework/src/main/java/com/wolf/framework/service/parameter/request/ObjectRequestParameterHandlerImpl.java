package com.wolf.framework.service.parameter.request;

import com.wolf.framework.service.parameter.ObjectRequestHandlerInfo;
import com.wolf.framework.service.parameter.RequestDataType;
import com.wolf.framework.service.parameter.RequestParameterHandler;
import java.util.Map;

/**
 * object类型处理类
 *
 * @author jianying9
 */
public final class ObjectRequestParameterHandlerImpl extends AbstractObjectRequestParameterHandler implements RequestParameterHandler {

    private final String errorInfo = " must be object";

    public ObjectRequestParameterHandlerImpl(
            final String name,
            boolean ignoreEmpty,
            ObjectRequestHandlerInfo objectRequestHandlerInfo
    ) {
        super(name, ignoreEmpty, objectRequestHandlerInfo);
    }

    @Override
    public String validate(final Object value) {
        String msg = this.errorInfo;
        if (Map.class.isInstance(value)) {
            Map<String, Object> valueMap = (Map<String, Object>) value;
            //验证必填参数
            msg = this.validateRequiredParameter(valueMap);
            if (msg.isEmpty()) {
                msg = this.validateUnrequiredParameter(valueMap);
            }
        }
        return msg;
    }

    @Override
    public RequestDataType getDataType() {
        return RequestDataType.OBJECT;
    }

}
