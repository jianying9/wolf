package com.wolf.framework.dao.delete;

import java.util.List;
import net.sf.ehcache.Cache;

/**
 *
 * @author aladdin
 */
public class DeleteEntityCacheHandlerImpl implements DeleteHandler {

    private final Cache entityCache;
    private final DeleteHandler deleteHandler;

    public DeleteEntityCacheHandlerImpl(Cache entityCache, DeleteHandler deleteHandler) {
        this.entityCache = entityCache;
        this.deleteHandler = deleteHandler;
    }

    @Override
    public void delete(String keyValue) {
        this.deleteHandler.delete(keyValue);
        this.entityCache.removeQuiet(keyValue);
    }

    @Override
    public void batchDelete(List<String> keyValues) {
        this.deleteHandler.batchDelete(keyValues);
        for (String keyValue : keyValues) {
            this.entityCache.removeQuiet(keyValue);
        }
    }
}
