package com.wolf.framework.dao.elasticsearch;

import com.wolf.framework.dao.ColumnDataType;
import com.wolf.framework.dao.ColumnHandlerImpl;
import com.wolf.framework.dao.ColumnType;
import com.wolf.framework.dao.FieldUtils;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 *
 * @author aladdin
 */
public class EsColumnHandlerImpl extends ColumnHandlerImpl implements EsColumnHandler {

    private final boolean analyzer;

    public EsColumnHandlerImpl(boolean analyzer, String columnName, String dataMap, Field field, ColumnType columnType, String desc, String defaultValue) {
        super(columnName, dataMap, field, columnType, desc, defaultValue);
        if (columnDataType.equals(ColumnDataType.STRING)) {
            this.analyzer = analyzer;
        } else {
            this.analyzer = false;
            if (columnDataType.equals(ColumnDataType.MAP)) {
                throw new RuntimeException("EsEntity not support this type:map");
            } else if (columnDataType.equals(ColumnDataType.SET)) {
                throw new RuntimeException("EsEntity not support this type:set");
            }
        }
    }

    public EsColumnHandlerImpl(String columnName, String dataMap, Field field, ColumnType columnType, String desc, String defaultValue) {
        super(columnName, dataMap, field, columnType, desc, defaultValue);
        this.analyzer = false;
    }

    @Override
    public boolean isAnalyzer() {
        return this.analyzer;
    }

    private EsColumnDataType getParameterDataType(ColumnDataType columnDataType) {
        EsColumnDataType esColumnDataType = null;
        switch (columnDataType) {
            case LONG:
            case INT:
                esColumnDataType = EsColumnDataType.LONG;
                break;
            case DOUBLE:
                esColumnDataType = EsColumnDataType.DOUBLE;
                break;
            case STRING:
                esColumnDataType = EsColumnDataType.KEYWORD;
                break;
            case BOOLEAN:
                esColumnDataType = EsColumnDataType.BOOLEAN;
                break;
        }
        return esColumnDataType;
    }

    @Override
    public EsColumnDataType getEsColumnDataType() {
        EsColumnDataType esColumnDataType = null;
        switch (this.columnDataType) {
            case LONG:
            case INT:
                esColumnDataType = EsColumnDataType.LONG;
                break;
            case BOOLEAN:
                esColumnDataType = EsColumnDataType.BOOLEAN;
                break;
            case DOUBLE:
                esColumnDataType = EsColumnDataType.DOUBLE;
                break;
            case STRING:
                if (this.analyzer) {
                    esColumnDataType = EsColumnDataType.TEXT;
                } else {
                    esColumnDataType = EsColumnDataType.KEYWORD;
                }
                break;
            case LIST:
                ParameterizedType listGenericType = (ParameterizedType) this.field.getGenericType();
                Type[] listActualTypeArguments = listGenericType.getActualTypeArguments();
                ColumnDataType parameterDataType = FieldUtils.getColumnDataType(listActualTypeArguments[0].getTypeName());
                esColumnDataType = this.getParameterDataType(parameterDataType);
                break;
            default:
                throw new RuntimeException("EsEntity not support this type:" + this.columnDataType.name());
        }
        return esColumnDataType;
    }

}
