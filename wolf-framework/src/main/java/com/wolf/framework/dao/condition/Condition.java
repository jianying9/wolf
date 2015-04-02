package com.wolf.framework.dao.condition;

/**
 *
 * @author aladdin
 */
public final class Condition {

    private final String columnName;
    private final OperateType operateType;
    private String columnValue;

    public Condition(String columnName, OperateType operateType, String columnValue) {
        this.columnName = columnName;
        this.operateType = operateType;
        if (this.operateType == OperateType.LIKE) {
            StringBuilder builder = new StringBuilder(columnValue.length() + 2);
            builder.append('%').append(columnValue).append('%');
            this.columnValue = builder.toString();
        } else {
            this.columnValue = columnValue;
        }
    }

    public String getColumnName() {
        return columnName;
    }

    public OperateType getOperateType() {
        return operateType;
    }

    public String getColumnValue() {
        return columnValue;
    }

    public void setColumnValue(String columnValue) {
        this.columnValue = columnValue;
    }
}
