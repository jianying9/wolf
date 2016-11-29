package com.wolf.framework.service.parameter.request;

import com.wolf.framework.data.DataType;
import com.wolf.framework.service.parameter.RequestParameterHandler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则类型处理类
 *
 * @author aladdin
 */
public final class RegexRequestParameterHandlerImpl implements RequestParameterHandler {

    private final String name;
    private final Pattern pattern;
    private final String errorInfo;

    public RegexRequestParameterHandlerImpl(String name, String text) {
        this.name = name;
        this.errorInfo = " must be regex(" + text + ")";
        this.pattern = Pattern.compile(text);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public DataType getDataType() {
        return DataType.REGEX;
    }

    @Override
    public String validate(String value) {
        String msg = "";
        Matcher matcher = this.pattern.matcher(value);
        if(matcher.matches() == false) {
            //不匹配
            msg = this.errorInfo;
        }
        return msg;
    }
}
