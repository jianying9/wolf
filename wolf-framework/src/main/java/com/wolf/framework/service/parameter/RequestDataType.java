package com.wolf.framework.service.parameter;

/**
 * data type
 *
 * @author jianying9
 */
public enum RequestDataType {
    EXTEND,
    //
    LONG,
    //
    DOUBLE,
    //DateTime {YYYY-MM-DD HH:MI,YYYY-MM-DD HH:MI:SS}
    DATE_TIME,
    //Date {YYYY-MM-DD,YYYY-m-d}
    DATE,
    //STRING可以包含特殊符号
    STRING,
    //枚举
    ENUM,
    //枚举
    REGEX,
    //中国地区手机号
    CHINA_MOBILE,
    //邮箱
    EMAIL,
    //布尔
    BOOLEAN,
    //
    JSON_OBJECT,
    //
    JSON_ARRAY,
    //
    OBJECT,
    //
    OBJECT_ARRAY,
    //
    STRING_ARRAY,
    //
    LONG_ARRAY;

}
