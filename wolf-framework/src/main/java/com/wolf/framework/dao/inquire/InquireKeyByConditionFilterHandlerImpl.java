package com.wolf.framework.dao.inquire;

import com.wolf.framework.dao.condition.InquireContext;
import com.wolf.framework.dao.parser.ColumnHandler;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aladdin
 */
public final class InquireKeyByConditionFilterHandlerImpl extends AbstractInquireConditionHandler implements InquireKeyByConditionHandler {

    private final InquireKeyByConditionHandler inquireKeyByConditionHandler;

    public InquireKeyByConditionFilterHandlerImpl(InquireKeyByConditionHandler inquireKeyByConditionHandler, List<ColumnHandler> columnHandlerList) {
        super(columnHandlerList);
        this.inquireKeyByConditionHandler = inquireKeyByConditionHandler;
    }

    @Override
    public List<String> inquireByConditon(InquireContext inquireContext) {
        List<String> result;
        this.filterCondition(inquireContext);
        if (inquireContext.hasCondition()) {
            result = this.inquireKeyByConditionHandler.inquireByConditon(inquireContext);
        } else {
            result = new ArrayList<String>(0);
        }
        return result;
    }
}
