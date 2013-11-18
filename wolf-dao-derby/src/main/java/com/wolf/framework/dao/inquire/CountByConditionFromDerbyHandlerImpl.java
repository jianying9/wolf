package com.wolf.framework.dao.inquire;

import com.wolf.framework.dao.condition.Condition;
import com.wolf.framework.dao.inquire.CountByConditionHandler;
import com.wolf.framework.derby.DerbyHandler;
import java.util.List;

/**
 *
 * @author aladdin
 */
public final class CountByConditionFromDerbyHandlerImpl implements CountByConditionHandler {

    private final DerbyHandler derbyHandler;

    public CountByConditionFromDerbyHandlerImpl(DerbyHandler derbyHandler) {
        this.derbyHandler = derbyHandler;
    }

    @Override
    public int count(List<Condition> conditionList) {
        return this.derbyHandler.count(conditionList);
    }
}
