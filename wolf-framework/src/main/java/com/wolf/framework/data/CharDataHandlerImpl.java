package com.wolf.framework.data;

import com.wolf.framework.utils.StringUtils;
import java.util.UUID;

/**
 * 字符数据类型抽象类
 *
 * @author aladdin
 */
public class CharDataHandlerImpl implements DataHandler {

    private final String error_message;
    private final int length;
    private final TypeEnum dataTypeEnum;

    CharDataHandlerImpl(String error_message, int length, TypeEnum dataTypeEnum) {
        this.error_message = error_message;
        this.length = length;
        this.dataTypeEnum = dataTypeEnum;
    }

    @Override
    public final String getDefaultValue() {
        return DataHandler.DEFAULT_CHAR_VALUE;
    }

    @Override
    public final String validate(final String value) {
        return value.length() <= this.length ? "" : this.error_message;
    }

    @Override
    public String getRandomValue() {
        return StringUtils.getRandomStringValue(this.length);
    }

    @Override
    public TypeEnum getDataTypeEnum() {
        return this.dataTypeEnum;
    }

    @Override
    public String getNextValue() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String convertToInput(String value) {
        return value;
    }

    @Override
    public String convertToOutput(String value) {
        return value;
    }
}
