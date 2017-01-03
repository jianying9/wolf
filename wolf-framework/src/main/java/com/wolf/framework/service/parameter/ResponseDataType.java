package com.wolf.framework.service.parameter;

/**
 * data type
 *
 * @author jianying9
 */
public enum ResponseDataType {

    LONG,
    
    DOUBLE,
    //DateTime {YYYY-MM-DD HH:MI,YYYY-MM-DD HH:MI:SS}
    DATE_TIME,
    //Date {YYYY-MM-DD,YYYY-m-d}
    DATE,
    //STRING可以包含特殊符号
    STRING,
    //枚举
    ENUM,
    //中国地区手机号
    CHINA_MOBILE,
    //邮箱
    EMAIL,
    //布尔
    BOOLEAN,
    //json对象
    OBJECT,
    //json数组
    OBJECT_ARRAY,
    //
    STRING_ARRAY,
    //
    LONG_ARRAY;
}
