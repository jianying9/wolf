package com.wolf.framework.service.parameter.request;

import com.wolf.framework.service.parameter.RequestDataType;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.wolf.framework.service.parameter.RequestHandler;

/**
 * 正则类型处理类
 *
 * @author aladdin
 */
public final class RegexRequestHandlerImpl implements RequestHandler {

    private final String name;
    private final Pattern pattern;
    private final String errorInfo;
    private final boolean ignoreEmpty;

    public RegexRequestHandlerImpl(String name, String text, boolean ignoreEmpty) {
        this.name = name;
        this.pattern = Pattern.compile(text);
        this.errorInfo = " must be regex";
        this.ignoreEmpty = ignoreEmpty;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public RequestDataType getDataType() {
        return RequestDataType.REGEX;
    }

    @Override
    public final String validate(Object value) {
        String result = this.errorInfo;
        if (String.class.isInstance(value)) {
            Matcher matcher = this.pattern.matcher((String) value);
            if (matcher.matches()) {
                result = "";
            }
        }
        return result;
    }

    @Override
    public boolean getIgnoreEmpty() {
        return this.ignoreEmpty;
    }

}
