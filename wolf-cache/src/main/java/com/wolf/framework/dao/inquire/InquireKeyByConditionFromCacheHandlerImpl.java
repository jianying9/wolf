package com.wolf.framework.dao.inquire;

import com.wolf.framework.dao.cache.InquireCache;
import com.wolf.framework.dao.condition.InquireContext;
import com.wolf.framework.dao.inquire.InquireKeyByConditionHandler;
import java.util.List;

/**
 *
 * @author aladdin
 */
public final class InquireKeyByConditionFromCacheHandlerImpl implements InquireKeyByConditionHandler {

    private final InquireCache inquireCache;
    private final InquireKeyByConditionHandler inquireKeyByConditionHandler;

    public InquireKeyByConditionFromCacheHandlerImpl(InquireCache inquireCache, InquireKeyByConditionHandler inquireKeyByConditionHandler) {
        this.inquireCache = inquireCache;
        this.inquireKeyByConditionHandler = inquireKeyByConditionHandler;
    }

    @Override
    public List<String> inquireByConditon(InquireContext inquireContext) {
        List<String> result = this.inquireCache.getInquireKeysCache(inquireContext);
        if (result == null) {
            result = this.inquireKeyByConditionHandler.inquireByConditon(inquireContext);
            this.inquireCache.putInquireKeysCache(inquireContext, result);
        }
        return result;
    }
}
