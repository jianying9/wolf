package com.wolf.framework.dao.inquire;

import com.wolf.framework.dao.condition.Condition;
import com.wolf.framework.dao.parser.ColumnHandler;
import java.util.List;

/**
 *
 * @author aladdin
 */
public final class CountByConditionFilterHandlerImpl extends AbstractInquireConditionHandler implements CountByConditionHandler {

    private final CountByConditionHandler countByConditionHandler;

    public CountByConditionFilterHandlerImpl(List<ColumnHandler> columnHandlerList, CountByConditionHandler countByConditionHandler) {
        super(columnHandlerList);
        this.countByConditionHandler = countByConditionHandler;
    }

    @Override
    public int count(List<Condition> conditionList) {
        int result;
        List<Condition> resultList = this.filterCondition(conditionList);
        if (resultList.isEmpty()) {
            result = 0;
        } else {
            result = this.countByConditionHandler.count(resultList);
        }
        return result;
    }
}
