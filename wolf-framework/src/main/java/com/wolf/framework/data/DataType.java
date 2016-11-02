package com.wolf.framework.data;

/**
 * data type
 *
 * @author jianying9
 */
public enum DataType {

    //BigIntSigned [-9223372036854775808,9223372036854775807]
    LONG,
    //Double [-1.7976931348623157×10+308, -4.94065645841246544×10-324]
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
    BOOLEAN ,
    //json对象
    OBJECT ,
    //json数组
    ARRAY ;

}
