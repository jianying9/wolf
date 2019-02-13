package com.wolf.framework.dao;

/**
 *
 * @author jianying9
 */
public interface ColumnHandler {

    public String getColumnName();

    public String getDataMap();

    public ColumnType getColumnType();

    public ColumnDataType getColumnDataType();

    public String getDesc();

    public Object getDefaultValue();

    public Object getFieldValue(Object object);

    public void setFieldValue(Object object, Object value);

    public ColumnDataType getFirstParameterDataType();

    public ColumnDataType getSecondParameterDataType();
}
