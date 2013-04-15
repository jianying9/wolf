package com.wolf.framework.data;

import com.wolf.framework.utils.NumberUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数字类型-999999999到999999999
 *
 * @author aladdin
 */
public final class IntDataHandlerImpl implements DataHandler {

    private final String error_message = " must be int[-999999999~999999999]";
    private final Pattern pattern = Pattern.compile("^\\d|[1-9]\\d{1,8}|-[1-9]\\d{0,8}$");

    IntDataHandlerImpl() {
    }

    @Override
    public String getRandomValue() {
        int value = NumberUtils.getRandomIntegerValue();
        return Integer.toString(value);
    }

    @Override
    public String validate(String value) {
        Matcher matcher = this.pattern.matcher(value);
        boolean result = matcher.matches();
        return result ? "" : this.error_message;
    }

    @Override
    public String getDefaultValue() {
        return DataHandler.DEFAULT_NUMBER_VALUE;
    }

    public DataTypeEnum getDataTypeEnum() {
        return DataTypeEnum.INT;
    }

    public String getNextValue() {
        return this.getRandomValue();
    }
    
    public String convertToInput(String value) {
        return value;
    }

    public String convertToOutput(String value) {
        return value;
    }
}
