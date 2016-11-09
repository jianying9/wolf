package com.wolf.framework.service.parameter.response;

import com.wolf.framework.data.DataType;
import com.wolf.framework.service.parameter.ResponseParameterHandler;

/**
 * parameter处理抽象类
 *
 * @author aladdin
 */
public abstract class AbstractResponseParameterHandler implements ResponseParameterHandler {

    protected final String name;
    protected final DataType dataType;

    protected AbstractResponseParameterHandler(final String name, final DataType dataType) {
        this.name = name;
        this.dataType = dataType;
    }

    @Override
    public final String getName() {
        return this.name;
    }

    @Override
    public final DataType getDataType() {
        return this.dataType;
    }
}
