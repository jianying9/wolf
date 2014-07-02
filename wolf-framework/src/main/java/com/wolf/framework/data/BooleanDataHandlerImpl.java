package com.wolf.framework.data;

import com.wolf.framework.utils.NumberUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * boolean:true|false
 *
 * @author aladdin
 */
public final class BooleanDataHandlerImpl implements DataHandler {

    private final String error_message = " must be boolean[true,false]";
    private final Pattern pattern = Pattern.compile("^true|false$");

    BooleanDataHandlerImpl() {
    }
    
    @Override
    public String getRandomValue() {
        long value = NumberUtils.getRandomLongValue();
        boolean result;
        if(value % 2 == 0) {
            result = true;
        } else {
            result = false;
        }
        return Boolean.toString(result);
    }

    @Override
    public String validate(String value) {
        Matcher matcher = this.pattern.matcher(value);
        boolean result = matcher.matches();
        return result ? "" : this.error_message;
    }

    @Override
    public TypeEnum getDataTypeEnum() {
        return TypeEnum.BOOLEAN;
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
