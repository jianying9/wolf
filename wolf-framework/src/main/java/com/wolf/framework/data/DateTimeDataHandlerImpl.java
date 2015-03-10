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

    private final String errorInfo = " must be datetime";
    private final Pattern pattern = Pattern.compile("[1-9]\\d{3}-(?:0?[1-9]|1[0-2])-(?:0?[1-9]|[1-2]\\d|3[0-1]) (?:[0-1]\\d|2[0-3]):[0-5]\\d(?::[0-5]\\d)?");

    DateTimeDataHandlerImpl() {
    }

    @Override
    public final boolean validate(final String value) {
        Matcher matcher = this.pattern.matcher(value);
        return matcher.matches();
    }
    
    @Override
    public String getErrorInfo() {
        return this.errorInfo;
    }

    @Override
    public DataType getDataType() {
        return DataType.DATE_TIME;
    }

    @Override
    public String convertToInput(String value) {
        long result = TimeUtils.convertYYYYMMDDHHmmSSToMillisecond(value);
        return Long.toString(result);
    }

    @Override
    public String convertToOutput(String value) {
        String result;
        if (value.isEmpty()) {
            result = value;
        } else {
            long milliseconds = Long.parseLong(value);
            result = TimeUtils.convertMillisecondToYYYYMMDDHHmmSS(milliseconds);
        }
        return result;
    }
}
