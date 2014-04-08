package com.wolf.framework.data;

import com.wolf.framework.utils.NumberUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数字类型-9999999999999999.999999到9999999999999999.999999
 *
 * @author aladdin
 */
public final class DoubleDataHandlerImpl implements DataHandler {

    private final String error_message = " must be double[-9999999999999999.999999,9999999999999999.999999]";
    private final Pattern pattern = Pattern.compile("^\\d(\\.\\d{1,6})?|[1-9]\\d{1,15}(\\.\\d{1,6})?|-[1-9]\\d{0,15}(\\.\\d{1,24})?$");

    DoubleDataHandlerImpl() {
    }

    @Override
    public String getRandomValue() {
        double value = NumberUtils.getRandomDoubleValue();
        String result = NumberUtils.NUMBER_FORMAT.format(value);
        return result;
    }

    @Override
    public String validate(String value) {
        Matcher matcher = this.pattern.matcher(value);
        boolean result = matcher.matches();
        return result ? "" : this.error_message;
    }

    @Override
    public String getDefaultValue() {
        return DataHandler.DEFAULT_DECIMAL_VALUE;
    }

    @Override
    public TypeEnum getDataTypeEnum() {
        return TypeEnum.DOUBLE;
    }

    @Override
    public String getNextValue() {
        return this.getRandomValue();
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
