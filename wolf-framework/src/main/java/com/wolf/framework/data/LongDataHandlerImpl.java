package com.wolf.framework.data;

import com.wolf.framework.utils.NumberUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数字类型-999999999999999999到999999999999999999
 *
 * @author aladdin
 */
public final class LongDataHandlerImpl implements DataHandler {

    private final String error_message = " must be long[-999999999999999999,999999999999999999]";
    private final Pattern pattern = Pattern.compile("^\\d|[1-9]\\d{1,17}|-[1-9]\\d{0,17}$");

    LongDataHandlerImpl() {
    }
    
    @Override
    public String getRandomValue() {
        long value = NumberUtils.getRandomLongValue();
        return Long.toString(value);
    }

    @Override
    public String validate(String value) {
        Matcher matcher = this.pattern.matcher(value);
        boolean result = matcher.matches();
        return result ? "" : this.error_message;
    }

    @Override
    public TypeEnum getDataTypeEnum() {
        return TypeEnum.LONG;
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
