package com.wolf.framework.dao.parser;

import com.wolf.framework.dao.annotation.ColumnTypeEnum;
import com.wolf.framework.data.DataHandler;

/**
 *
 * @author aladdin
 */
public class ColumnHandlerImpl implements ColumnHandler {

    private final String columnName;
    private final ColumnTypeEnum columnTypeEnum;
    private final String desc;
    private final DataHandler dataHandler;

    public ColumnHandlerImpl(String columnName, ColumnTypeEnum columnTypeEnum, String desc, DataHandler dataHandler) {
        this.columnName = columnName;
        this.columnTypeEnum = columnTypeEnum;
        this.desc = desc;
        this.dataHandler = dataHandler;
    }

    @Override
    public String getColumnName() {
        return this.columnName;
    }

    @Override
    public ColumnTypeEnum getColumnType() {
        return this.columnTypeEnum;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }

    @Override
    public DataHandler getDataHandler() {
        return this.dataHandler;
    }
}
