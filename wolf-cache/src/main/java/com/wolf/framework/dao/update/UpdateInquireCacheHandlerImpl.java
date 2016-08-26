package com.wolf.framework.dao.update;

import com.wolf.framework.dao.cache.InquireCache;
import com.wolf.framework.dao.update.UpdateHandler;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public class UpdateInquireCacheHandlerImpl implements UpdateHandler {

    private final InquireCache inquireAndCountCache;
    private final UpdateHandler updateHandler;

    public UpdateInquireCacheHandlerImpl(InquireCache inquireAndCountCache, UpdateHandler updateHandler) {
        this.inquireAndCountCache = inquireAndCountCache;
        this.updateHandler = updateHandler;
    }

    @Override
    public String update(Map<String, String> entityMap) {
        String keyValue = this.updateHandler.update(entityMap);
        this.inquireAndCountCache.removeCache();
        return keyValue;
    }

    @Override
    public void batchUpdate(List<Map<String, String>> entityMapList) {
        this.updateHandler.batchUpdate(entityMapList);
        this.inquireAndCountCache.removeCache();
    }
}
