package com.wolf.framework.dao.inquire;

import com.wolf.framework.dao.condition.InquireContext;
import com.wolf.framework.derby.DerbyHandler;
import java.util.List;

/**
 *
 * @author aladdin
 */
public final class InquireKeyByConditionFromDerbyHandlerImpl implements InquireKeyByConditionHandler {

    private final DerbyHandler derbyHandler;

    public InquireKeyByConditionFromDerbyHandlerImpl(DerbyHandler derbyHandler) {
        this.derbyHandler = derbyHandler;
    }

    @Override
    public List<String> inquireByConditon(InquireContext inquireContext) {
        return this.derbyHandler.inquireKeys(inquireContext);
    }
}
