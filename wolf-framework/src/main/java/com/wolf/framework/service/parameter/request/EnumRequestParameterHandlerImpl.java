package com.wolf.framework.service.parameter.request;

import com.wolf.framework.data.DataType;
import com.wolf.framework.service.parameter.RequestParameterHandler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 枚举类型处理类
 *
 * @author aladdin
 */
public final class EnumRequestParameterHandlerImpl implements RequestParameterHandler {

    private final String name;
    private final Pattern pattern;
    private final String errorInfo;

    public EnumRequestParameterHandlerImpl(String name, String[] enumValues) {
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
