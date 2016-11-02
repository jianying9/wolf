package com.wolf.framework.service.parameter;

import com.wolf.framework.data.DataType;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 枚举类型处理类
 *
 * @author aladdin
 */
public final class EnumParameterHandlerImpl implements RequestParameterHandler, ResponseParameterHandler {

    private final String name;
    private final Pattern pattern;
    private final String errorInfo;

    public EnumParameterHandlerImpl(String name, String[] enumValues) {
        this.name = name;
        StringBuilder stringBuilder = new StringBuilder();
        for (String value : enumValues) {
            stringBuilder.append(value).append("|");
        }
        stringBuilder.setLength(stringBuilder.length() - 1);
        String enumStr = stringBuilder.toString();
        this.errorInfo = " must be enum[" + enumStr + "]";
        this.pattern = Pattern.compile("^" + enumStr + "$");
    }

    @Override
    public String getJson(String value) {
        String result;
        StringBuilder jsonBuilder = new StringBuilder(this.name.length() + value.length() + 5);
        jsonBuilder.append('"').append(name).append("\":\"").append(value).append("\"");
        result = jsonBuilder.toString();
        return result;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public DataType getDataType() {
        return DataType.ENUM;
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
