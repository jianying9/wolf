package com.wolf.framework.dao.inquire;

import com.wolf.framework.dao.Entity;
import com.wolf.framework.dao.condition.InquireContext;
import java.util.List;

/**
 *
 * @author aladdin
 * @param <T>
 */
public interface InquireByConditionHandler<T extends Entity> {

    public List<T> inquireByConditon(InquireContext inquireContext);
}
