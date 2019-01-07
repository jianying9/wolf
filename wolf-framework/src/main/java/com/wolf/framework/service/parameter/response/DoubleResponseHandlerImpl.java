package com.wolf.framework.service.parameter.response;

import com.wolf.framework.service.parameter.*;
import java.math.BigDecimal;

/**
 * double类型处理类
 *
 * @author aladdin
 */
public final class DoubleResponseHandlerImpl implements ResponseHandler {

    private final String name;

    public DoubleResponseHandlerImpl(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ResponseDataType getDataType() {
        return ResponseDataType.DOUBLE;
    }

    @Override
    public Object getResponseValue(Object value) {
        if (Double.class.isInstance(value) == false) {
            String errMsg = "response:" + this.name + "'s type is not Double.";
            throw new RuntimeException(errMsg);
        }
        Object result;
        //如果小数位为0,则转整形
        Double d = (Double) value;
        if (d % 1 == 0) {
            result = d.longValue();
        } else {
            result = new BigDecimal(d).setScale(10, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return result;
    }

}
