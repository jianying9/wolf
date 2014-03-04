package com.wolf.framework.dao.inquire;

import com.wolf.framework.dao.cache.InquireCache;
import com.wolf.framework.dao.condition.Condition;
import java.util.List;

/**
 *
 * @author aladdin
 */
public final class CountByConditionFromCacheHandlerImpl implements CountByConditionHandler {

    private final InquireCache inquireCache;
    private final CountByConditionHandler countByConditionHandler;

    public CountByConditionFromCacheHandlerImpl(InquireCache inquireCache, CountByConditionHandler countByConditionHandler) {
        this.inquireCache = inquireCache;
        this.countByConditionHandler = countByConditionHandler;
    }

    @Override
    public int count(List<Condition> conditionList) {
        Integer result = this.inquireCache.getCountCache(conditionList);
        if (result == null) {
            //从数据源获取
            result = this.countByConditionHandler.count(conditionList);
            this.inquireCache.putCountCache(conditionList, result);
        }
        return result;
    }
}
