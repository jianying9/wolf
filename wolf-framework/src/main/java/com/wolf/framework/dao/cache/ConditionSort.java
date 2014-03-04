package com.wolf.framework.dao.cache;

import com.wolf.framework.dao.condition.Condition;
import java.util.Comparator;

/**
 *
 * @author aladdin
 */
public class ConditionSort implements Comparator<Condition> {

    @Override
    public int compare(Condition o1, Condition o2) {
        int result = o1.getColumnName().compareTo(o2.getColumnName());
        if (result == 0) {
            result = o1.getColumnValue().compareTo(o2.getColumnValue());
        }
        return result;
    }
}
