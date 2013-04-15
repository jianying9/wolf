package com.wolf.framework.dao.insert;

import com.wolf.framework.dao.Entity;
import com.wolf.framework.dao.cache.InquireCache;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public class InsertCacheHandlerImpl<T extends Entity> implements InsertHandler<T> {

    private final InquireCache inquireCache;
    private final InsertHandler<T> insertHandler;

    public InsertCacheHandlerImpl(InquireCache inquireCache, InsertHandler<T> insertHandler) {
        this.inquireCache = inquireCache;
        this.insertHandler = insertHandler;
    }

    @Override
    public String insert(Map<String, String> entityMap) {
        String keyValue = this.insertHandler.insert(entityMap);
        this.inquireCache.removeCache();
        return keyValue;
    }

    @Override
    public T insertAndInquire(Map<String, String> entityMap) {
        T t = this.insertHandler.insertAndInquire(entityMap);
        this.inquireCache.removeCache();
        return t;
    }

    @Override
    public void batchInsert(List<Map<String, String>> entityMapList) {
        this.insertHandler.batchInsert(entityMapList);
        this.inquireCache.removeCache();
    }
}
