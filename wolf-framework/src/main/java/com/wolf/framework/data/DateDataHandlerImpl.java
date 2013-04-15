package com.wolf.framework.data;

import com.wolf.framework.utils.TimeUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 *
 * @author aladdin
 */
public class DateDataHandlerImpl implements DataHandler {

    private final String error_message = " must be date";
    private final Pattern pattern = Pattern.compile("[1-9]\\d{3}-(?:0?[1-9]|1[0-2])-(?:0?[1-9]|[1-2]\\d|3[0-1])");

    DateDataHandlerImpl() {
    }

    @Override
    public final String getDefaultValue() {
        return DataHandler.DEFAULT_DATE_VALUE;
    }

    @Override
    public final String validate(final String value) {
        Matcher matcher = this.pattern.matcher(value);
        boolean result = matcher.matches();
        return result ? "" : this.error_message;
    }

    @Override
    public String getRandomValue() {
        return TimeUtils.getDateFotmatYYMMDD();
    }

    public DataTypeEnum getDataTypeEnum() {
        return DataTypeEnum.DATE;
    }

    public String getNextValue() {
        return this.getRandomValue();
    }
    
    public String convertToInput(String value) {
        long result = TimeUtils.convertYYYYMMDDToMillisecond(value);
        return Long.toString(result);
    }

    public String convertToOutput(String value) {
        long milliseconds = Long.parseLong(value);
        String result = TimeUtils.convertMillisecondToYYYYMMDD(milliseconds);
        return result;
    }
}
