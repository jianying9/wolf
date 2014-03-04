package com.wolf.framework.dao.inquire;

import com.wolf.framework.dao.Entity;
import com.wolf.framework.dao.condition.InquireContext;
import com.wolf.framework.dao.parser.ColumnHandler;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aladdin
 */
public final class InquireByConditionFilterHandlerImpl<T extends Entity> extends AbstractInquireConditionHandler implements InquireByConditionHandler<T> {

    private final InquireByConditionHandler<T> inquireByConditionHandler;

    public InquireByConditionFilterHandlerImpl(List<ColumnHandler> columnHandlerList, InquireByConditionHandler<T> inquireByConditionHandler) {
        super(columnHandlerList);
        this.inquireByConditionHandler = inquireByConditionHandler;
    }

    @Override
    public List<T> inquireByConditon(InquireContext inquireContext) {
        List<T> result;
        this.filterCondition(inquireContext);
        if (inquireContext.hasCondition()) {
            result = this.inquireByConditionHandler.inquireByConditon(inquireContext);
        } else {
            result = new ArrayList<T>(0);
        }
        return result;
    }
}
