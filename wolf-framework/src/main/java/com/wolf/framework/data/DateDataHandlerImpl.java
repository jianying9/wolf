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
    public final String validate(final String value) {
        Matcher matcher = this.pattern.matcher(value);
        boolean result = matcher.matches();
        return result ? "" : this.error_message;
    }

    @Override
    public String getRandomValue() {
        return TimeUtils.getDateFotmatYYMMDD();
    }

    @Override
    public TypeEnum getDataTypeEnum() {
        return TypeEnum.DATE;
    }

    @Override
    public String convertToInput(String value) {
        long result = TimeUtils.convertYYYYMMDDToMillisecond(value);
        return Long.toString(result);
    }

    @Override
    public String convertToOutput(String value) {
        String result;
        if (value.isEmpty()) {
            result = value;
        } else {
            long milliseconds = Long.parseLong(value);
            result = TimeUtils.convertMillisecondToYYYYMMDD(milliseconds);
        }
        return result;
    }
}
