package com.wolf.framework.dao.insert;

import com.wolf.framework.dao.cache.InquireCache;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public class InsertCacheHandlerImpl implements InsertHandler {

    private final InquireCache inquireCache;
    private final InsertHandler insertHandler;

    public InsertCacheHandlerImpl(InquireCache inquireCache, InsertHandler insertHandler) {
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
    public void batchInsert(List<Map<String, String>> entityMapList) {
        this.insertHandler.batchInsert(entityMapList);
        this.inquireCache.removeCache();
    }
}
