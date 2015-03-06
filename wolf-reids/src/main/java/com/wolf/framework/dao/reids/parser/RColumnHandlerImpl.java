package com.wolf.framework.dao.reids.parser;

import com.wolf.framework.dao.annotation.ColumnTypeEnum;
import com.wolf.framework.dao.parser.ColumnHandlerImpl;

/**
 *
 * @author aladdin
 */
public class RColumnHandlerImpl extends ColumnHandlerImpl implements RColumnHandler {

    public RColumnHandlerImpl(String columnName, ColumnTypeEnum columnTypeEnum, String desc, String defaultValue) {
        super(columnName, columnTypeEnum, desc, defaultValue);
    }
}
