package com.wolf.framework.dao.cassandra.annotation;

/**
 * data type
 *
 * @author aladdin
 */
public enum ColumnType {

    //主键
    KEY,
    //索引
    INDEX,
    //一般列
    COLUMN;
}
