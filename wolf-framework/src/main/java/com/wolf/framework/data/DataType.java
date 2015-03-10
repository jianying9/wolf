package com.wolf.framework.data;

/**
 * data type
 *
 * @author aladdin
 */
public enum DataType {

    //BigIntSigned [-9223372036854775808,9223372036854775807]
    INTEGER,
    //Double [-1.7976931348623157×10+308, -4.94065645841246544×10-324]
    DOUBLE,
    //DateTime {YYYY-MM-DD HH:MM,YYYY-MM-DD HH:MM:SS}
    DATE_TIME,
    //Date {YYYY-MM-DD,YYYY-m-d}
    DATE,
    //Char可以包含特殊符号
    CHAR,
    BOOLEAN ,
    //json对象
    OBJECT ,
    //json数组
    ARRAY ;

}
