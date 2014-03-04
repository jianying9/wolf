package com.wolf.framework.service.parameter.filter;

/**
 * 安全过滤
 *
 * @author aladdin
 */
public final class SecurityFilterImpl implements Filter {

    @Override
    public String doFilter(final String value) {
        String result = "";
        if (!value.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder(value.length());
            char cht;
            for (int index = 0; index < value.length(); index++) {
                cht = value.charAt(index);
                switch (cht) {
                    case '<':
                        stringBuilder.append('＜');
                        break;
                    case '>':
                        stringBuilder.append('＞');
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
