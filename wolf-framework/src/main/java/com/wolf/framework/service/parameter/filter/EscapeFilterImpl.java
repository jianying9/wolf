package com.wolf.framework.service.parameter.filter;

/**
 * 转义过滤
 *
 * @author aladdin
 */
public final class EscapeFilterImpl implements Filter {

    @Override
    public String doFilter(final String value) {
        String result = "";
        if (!value.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder(value.length() * 2);
            char cht;
            for (int index = 0; index < value.length(); index++) {
                cht = value.charAt(index);
                switch (cht) {
                    case '\"':
                        stringBuilder.append("\\\"");
                        break;
                    case '\\':
                        stringBuilder.append("\\\\");
                        break;
                    case '\b':
                        stringBuilder.append("\\b");
                        break;
                    case '\n':
                        stringBuilder.append("\\n");
                        break;
                    case '/':
                        stringBuilder.append("\\/");
                        break;
                    case '\f':
                        stringBuilder.append("\\f");
                        break;
                    case '\r':
                        stringBuilder.append("\\r");
                        break;
                    case '\t':
                        stringBuilder.append("\\t");
                        break;
                    default:
                        stringBuilder.append(cht);
                }
            }
            result = stringBuilder.toString();
        }
        return result;
    }
}
