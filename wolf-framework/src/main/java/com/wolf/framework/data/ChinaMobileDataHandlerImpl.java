package com.wolf.framework.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 中国移动号码
 *
 * @author jianying9
 */
public final class ChinaMobileDataHandlerImpl implements DataHandler {

    private final Pattern pattern = Pattern.compile("^1[3578]{1}\\d{9}\\d?$");
    private final String errorInfo = " must be china mobile";

    ChinaMobileDataHandlerImpl() {
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
        return DataType.CHINA_MOBILE;
    }
}
