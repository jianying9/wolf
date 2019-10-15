package com.wolf.framework.service.parameter.request;

import com.wolf.framework.service.parameter.RequestDataType;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.wolf.framework.service.parameter.RequestHandler;

/**
 * 枚举类型处理类
 *
 * @author aladdin
 */
public final class EnumRequestHandlerImpl implements RequestHandler {

    private final String name;
    private final Pattern pattern;
    private final String errorInfo;
    private final boolean ignoreEmpty;

    public EnumRequestHandlerImpl(String name, String[] enumValues, boolean ignoreEmpty) {
        this.name = name;
        StringBuilder stringBuilder = new StringBuilder();
        for (String value : enumValues) {
            stringBuilder.append(value).append("|");
        }
        stringBuilder.setLength(stringBuilder.length() - 1);
        String enumStr = stringBuilder.toString();
        this.errorInfo = " must be enum[" + enumStr + "]";
        this.pattern = Pattern.compile("^" + enumStr + "$");
        this.ignoreEmpty = ignoreEmpty;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public RequestDataType getDataType() {
        return RequestDataType.ENUM;
    }

    @Override
    public String validate(Object value) {
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
