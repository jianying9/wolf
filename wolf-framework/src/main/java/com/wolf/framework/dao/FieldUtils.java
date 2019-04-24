package com.wolf.framework.dao;

/**
 *
 * @author jianying9
 */
public class FieldUtils {

    public static ColumnDataType getColumnDataType(String type) {
        ColumnDataType dataType;
        switch (type) {
            case "long":
            case "java.lang.Long":
                dataType = ColumnDataType.LONG;
                break;
            case "int":
            case "java.lang.Integer":
                dataType = ColumnDataType.INT;
                break;
            case "boolean":
            case "java.lang.Boolean":
                dataType = ColumnDataType.BOOLEAN;
                break;
            case "double":
            case "java.lang.Double":
                dataType = ColumnDataType.DOUBLE;
                break;
            case "java.lang.String":
                dataType = ColumnDataType.STRING;
                break;
            case "java.util.List":
                dataType = ColumnDataType.LIST;
                break;
            case "java.util.Set":
                dataType = ColumnDataType.SET;
                break;
            case "java.util.Map":
                dataType = ColumnDataType.MAP;
                break;
            default:
                throw new RuntimeException("Entity not support this type:" + type);
        }
        return dataType;
    }
}
