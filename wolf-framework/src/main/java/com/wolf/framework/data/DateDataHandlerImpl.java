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

    private final Pattern pattern = Pattern.compile("[1-9]\\d{3}-(?:0?[1-9]|1[0-2])-(?:0?[1-9]|[1-2]\\d|3[0-1])");
    private final String errorInfo = " must be date[yyyy-mm-dd]";
    

    DateDataHandlerImpl() {
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
        return DataType.DATE;
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
