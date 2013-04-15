package com.wolf.framework.dao.inquire;

import com.wolf.framework.dao.Entity;
import com.wolf.framework.dao.cache.InquireCache;
import com.wolf.framework.dao.condition.InquireContext;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aladdin
 */
public final class InquireByConditionFromCacheHandlerImpl<T extends Entity> implements InquireByConditionHandler<T> {
    
    private final String tableName;
    private final InquireCache inquireCache;
    private final InquireByKeyHandler<T> inquireByKeyHandler;
    private final InquireByConditionHandler<T> inquireByConditionHandler;
    
    public InquireByConditionFromCacheHandlerImpl(String tableName, InquireCache inquireCache, InquireByKeyHandler<T> inquireByKeyHandler, InquireByConditionHandler<T> inquireByConditionHandler) {
        this.tableName = tableName;
        this.inquireCache = inquireCache;
        this.inquireByKeyHandler = inquireByKeyHandler;
        this.inquireByConditionHandler = inquireByConditionHandler;
    }
    
    @Override
    public List<T> inquireByConditon(InquireContext inquireContext) {
        List<T> result;
        List<String> keyList = this.inquireCache.getInquireKeysCache(inquireContext);
        if (keyList == null) {
            //从数据源获取
            result = this.inquireByConditionHandler.inquireByConditon(inquireContext);
            //缓存
            if (result.isEmpty()) {
                keyList = new ArrayList<String>(0);
            } else {
                keyList = new ArrayList<String>(result.size());
                for (T t : result) {
                    keyList.add(t.getKeyValue());
                }
            }
            this.inquireCache.putInquireKeysCache(inquireContext, keyList);
        } else {
            //获取缓存key值
            result = this.inquireByKeyHandler.inquireByKeys(keyList);
        }
        return result;
    }
}
