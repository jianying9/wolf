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
public abstract class AbstractRegexRequestHandler implements RequestHandler {

    private final String name;
    private final RequestDataType dataType;
    private final Pattern pattern;
    private final String errorInfo;
    private final boolean ignoreEmpty;
    

    public AbstractRegexRequestHandler(String name, RequestDataType dataType, String text, String errorInfo, boolean ignoreEmpty) {
        this.name = name;
        this.pattern = Pattern.compile(text);
        this.dataType = dataType;
        this.errorInfo = errorInfo;
        this.ignoreEmpty = ignoreEmpty;
    }
    
    @Override
    public boolean getIgnoreEmpty() {
        return ignoreEmpty;
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
    public final String validate(Object value) {
        String result = this.errorInfo;
        if(String.class.isInstance(value)) {
            Matcher matcher = this.pattern.matcher((String) value);
            if(matcher.matches()) {
                result = "";
            }
        }
        return result;
    }
}
