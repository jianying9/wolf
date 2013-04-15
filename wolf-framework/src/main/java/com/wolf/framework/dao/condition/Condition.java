package com.wolf.framework.dao.condition;

/**
 *
 * @author aladdin
 */
public final class Condition {

    private final String columnName;
    private final OperateTypeEnum operateTypeEnum;
    private String columnValue;

    public Condition(String columnName, OperateTypeEnum operateTypeEnum, String columnValue) {
        this.columnName = columnName;
        this.operateTypeEnum = operateTypeEnum;
        this.columnValue = columnValue;
    }

    public String getColumnName() {
        return columnName;
    }

    public OperateTypeEnum getOperateTypeEnum() {
        return operateTypeEnum;
    }

    public String getColumnValue() {
        return columnValue;
    }

    public void setColumnValue(String columnValue) {
        this.columnValue = columnValue;
    }
}
