package com.wolf.framework.service.parameter.response;

import com.wolf.framework.service.parameter.ResponseDataType;
import com.wolf.framework.service.parameter.ResponseParameterHandler;
import com.wolf.framework.service.parameter.filter.Filter;
import java.util.List;

/**
 * string array类型处理类
 *
 * @author aladdin
 */
public final class StringArrayResponseParameterHandlerImpl implements ResponseParameterHandler {

    private final Filter[] filters;
    private final String name;

    public StringArrayResponseParameterHandlerImpl(String name, Filter[] filters) {
        this.name = name;
        this.filters = filters;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ResponseDataType getDataType() {
        return ResponseDataType.STRING_ARRAY;
    }

    private String getfilterValue(String value) {
        String result = value;
        if (this.filters != null) {
            for (Filter filter : filters) {
                result = filter.doFilter(result);
            }
        }
        return result;
    }

    @Override
    public Object getResponseValue(Object value) {
        boolean isStringArray = true;
        if (List.class.isInstance(value) == false) {
            isStringArray = false;
        } else {
            List<Object> objectList = (List<Object>) value;
            String str;
            Object obj;
            for (int i = 0; i < objectList.size(); i++) {
                obj = objectList.get(i);
                if (String.class.isInstance(obj) == false) {
                    isStringArray = false;
                    break;
                } else {
                    str = (String) obj;
                    str = this.getfilterValue(str);
                    objectList.set(i, str);
                }
            }
        }
        if (isStringArray == false) {
            String errMsg = "response:" + this.name + "'s type is not String array.";
            throw new RuntimeException(errMsg);
        }
        return value;
    }
}
