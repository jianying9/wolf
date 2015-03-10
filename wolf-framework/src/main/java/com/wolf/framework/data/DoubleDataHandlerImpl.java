package com.wolf.framework.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数字类型-9999999999999999.999999到9999999999999999.999999
 *
 * @author aladdin
 */
public final class DoubleDataHandlerImpl implements DataHandler {

    private final String errorInfo = " must be double";
    private final Pattern pattern = Pattern.compile("^\\d(\\.\\d{1,6})?|[1-9]\\d{1,15}(\\.\\d{1,6})?|-[1-9]\\d{0,15}(\\.\\d{1,24})?$");

    DoubleDataHandlerImpl() {
    }

    @Override
    public boolean validate(String value) {
        Matcher matcher = this.pattern.matcher(value);
        return matcher.matches();
    }
    
    @Override
    public String getErrorInfo() {
        return this.errorInfo;
    }

    @Override
    public DataType getDataType() {
        return DataType.DOUBLE;
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
