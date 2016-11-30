package com.wolf.framework.service.parameter.request;

import com.wolf.framework.service.parameter.RequestDataType;
import com.wolf.framework.service.parameter.RequestParameterHandler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则类型处理类
 *
 * @author aladdin
 */
public abstract class AbstractRegexRequestParameterHandler implements RequestParameterHandler {

    private final String name;
    private final RequestDataType dataType;
    private final Pattern pattern;
    private final String errorInfo;

    public AbstractRegexRequestParameterHandler(String name, RequestDataType dataType, String text, String errorInfo) {
        this.name = name;
        this.pattern = Pattern.compile(text);
        this.dataType = dataType;
        this.errorInfo = errorInfo;
    }

    @Override
    public final String getName() {
        return this.name;
    }

    @Override
    public final RequestDataType getDataType() {
        return this.dataType;
    }

    @Override
    public final String validate(String value) {
        Matcher matcher = this.pattern.matcher(value);
        return matcher.matches() ? "" : this.errorInfo;
    }
}
