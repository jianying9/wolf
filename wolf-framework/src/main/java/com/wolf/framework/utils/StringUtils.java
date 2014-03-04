package com.wolf.framework.utils;

/**
 * @author aladdin
 *
 */
public final class StringUtils {

    private StringUtils() {
    }

    public static String getRandomStringValue(int length) {
        String result = "";
        String[] model = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "g", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "G", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
            " ", ",", ".", "/", ";", "'", ":", "\"", "[", "]", "{", "}", "|", "\\", "?", "=", "+", "-", "_", "(", ")", "*", "&", "^", "%", "%", "#", "@", "!", "`", "~", "<", ">",
            "啊", "并", "凑", "的", "额", "非", "供", "和", "爱", "将", "库", "刘", "码", "呢", "哦", "陪", "其", "人", "色", "头", "有", "为", "问", "新", "已", "增"};
        int num = NumberUtils.getRandomIntegerValue(length);
        if (num > 0) {
            int valueIndex;
            StringBuilder valueBuilder = new StringBuilder(num);
            for (int index = 0; index < num; index++) {
                valueIndex = NumberUtils.getRandomIntegerValue(model.length);
                valueBuilder.append(model[valueIndex]);
            }
            result = valueBuilder.toString();
        }
        return result;
    }

    /**
     * 过滤前后全角半角空格
     *
     * @param value
     * @return
     */
    public static String trim(String value) {
        String result = "";
        int len = value.length();
        if (len > 0) {
            int st = 0;
            int end = len;      /* avoid getfield opcode */
            char[] val = value.toCharArray();    /* avoid getfield opcode */
            while ((st < end) && (val[st] == ' ' || val[st] == '　')) {
                st++;
            }
            while ((st < end) && (val[end - 1] == ' ' || val[end - 1] == '　')) {
                end--;
            }
            result = ((st > 0) || (end < len)) ? value.substring(st, end) : value;
        }
        return result;
    }
}
