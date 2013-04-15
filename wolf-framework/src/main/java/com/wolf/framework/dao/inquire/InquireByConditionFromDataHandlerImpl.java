package com.wolf.framework.dao.inquire;

import com.wolf.framework.dao.AbstractDaoHandler;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.dao.condition.InquireContext;
import com.wolf.framework.dao.parser.ColumnHandler;
import com.wolf.framework.derby.DerbyHandler;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public final class InquireByConditionFromDataHandlerImpl<T extends Entity> extends AbstractDaoHandler<T> implements InquireByConditionHandler<T> {

    private final DerbyHandler derbyHandler;

    public InquireByConditionFromDataHandlerImpl(DerbyHandler derbyHandler, String tableName, Class<T> clazz, List<ColumnHandler> columnHandlerList, ColumnHandler keyHandler) {
        super(tableName, clazz, columnHandlerList, keyHandler);
        this.derbyHandler = derbyHandler;
    }

    @Override
    public List<T> inquireByConditon(InquireContext inquireContext) {
        //查询
        List<Map<String, String>> entityMapList = this.derbyHandler.inquire(inquireContext);
        return this.newInstance(entityMapList);
    }
}
