package com.wolf.framework.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * boolean:true|false
 *
 * @author aladdin
 */
public final class BooleanDataHandlerImpl implements DataHandler {

    private final Pattern pattern = Pattern.compile("^true|false$");
    private final String errorInfo = " must be boolean";

    BooleanDataHandlerImpl() {
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
        return DataType.BOOLEAN;
    }
}
