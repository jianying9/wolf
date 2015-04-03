package com.wolf.framework.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数字类型-999999999999999999到999999999999999999
 *
 * @author aladdin
 */
public final class IntegerDataHandlerImpl implements DataHandler {

    private final String errorInfo = " must be integer";
    private final Pattern pattern = Pattern.compile("^\\d|[1-9]\\d{1,17}|-[1-9]\\d{0,17}$");

    IntegerDataHandlerImpl() {
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
        return DataType.INTEGER;
    }
}
