package com.wolf.framework.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 邮箱
 *
 * @author jianying9
 */
public final class EmailDataHandlerImpl implements DataHandler {

    private final Pattern pattern = Pattern.compile("^[a-z\\d]+[a-z\\d_]+@[a-z\\d]+\\.com$");
    private final String errorInfo = " must be email";

    EmailDataHandlerImpl() {
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
        return DataType.EMAIL;
    }
}
