package com.wolf.framework.dao.reids.parser;

import com.wolf.framework.dao.annotation.ColumnType;
import com.wolf.framework.dao.parser.ColumnHandlerImpl;

/**
 *
 * @author aladdin
 */
public class RColumnHandlerImpl extends ColumnHandlerImpl implements RColumnHandler {

    public RColumnHandlerImpl(String columnName, ColumnType columnTypeEnum, String desc, String defaultValue) {
        super(columnName, columnTypeEnum, desc, defaultValue);
    }
}
