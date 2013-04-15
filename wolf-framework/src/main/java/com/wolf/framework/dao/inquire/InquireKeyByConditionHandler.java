package com.wolf.framework.dao.inquire;

import com.wolf.framework.dao.condition.InquireContext;
import java.util.List;

/**
 *
 * @author aladdin
 */
public interface InquireKeyByConditionHandler {

    public List<String> inquireByConditon(InquireContext inquireContext);
}
