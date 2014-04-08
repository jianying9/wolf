package com.wolf.framework.data;

import com.wolf.framework.utils.TimeUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 *
 * @author aladdin
 */
public class DateTimeDataHandlerImpl implements DataHandler {

    private final String error_message = " must be dateTime";
    private final Pattern pattern = Pattern.compile("[1-9]\\d{3}-(?:0?[1-9]|1[0-2])-(?:0?[1-9]|[1-2]\\d|3[0-1]) (?:[0-1]\\d|2[0-3]):[0-5]\\d(?::[0-5]\\d)?");

    DateTimeDataHandlerImpl() {
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
        return TimeUtils.getDateFotmatYYMMDDHHmmSS();
    }

    @Override
    public TypeEnum getDataTypeEnum() {
        return TypeEnum.DATE_TIME;
    }

    @Override
    public String getNextValue() {
        return this.getRandomValue();
    }

    @Override
    public String convertToInput(String value) {
        long result = TimeUtils.convertYYYYMMDDHHmmSSToMillisecond(value);
        return Long.toString(result);
    }

    @Override
    public String convertToOutput(String value) {
        long milliseconds = Long.parseLong(value);
        String result = TimeUtils.convertMillisecondToYYYYMMDDHHmmSS(milliseconds);
        return result;
    }
}
