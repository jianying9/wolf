package com.wolf.framework.dao.inquire;

import com.wolf.framework.dao.condition.Condition;
import java.util.List;

/**
 *
 * @author aladdin
 */
public interface CountByConditionHandler {
    
    public int count(List<Condition> conditionList);
}
