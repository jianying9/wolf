package com.wolf.framework.dao.parser;

/**
 *
 * @author aladdin
 */
public class HColumnHandlerImpl implements HColumnHandler {

    private final String columnName;
    private final String desc;

    public HColumnHandlerImpl(String columnName, String desc) {
        this.columnName = columnName;
        this.desc = desc;
    }

    @Override
    public String getColumnName() {
        return this.columnName;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }
}
