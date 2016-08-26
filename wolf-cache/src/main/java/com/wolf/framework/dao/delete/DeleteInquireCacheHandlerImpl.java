package com.wolf.framework.dao.delete;

import com.wolf.framework.dao.cache.InquireCache;
import com.wolf.framework.dao.delete.DeleteHandler;
import java.util.List;

/**
 *
 * @author aladdin
 */
public class DeleteInquireCacheHandlerImpl implements DeleteHandler {

    private final InquireCache inquireAndCountCache;
    private final DeleteHandler deleteHandler;

    public DeleteInquireCacheHandlerImpl(InquireCache inquireAndCountCache, DeleteHandler deleteHandler) {
        this.inquireAndCountCache = inquireAndCountCache;
        this.deleteHandler = deleteHandler;
    }

    @Override
    public void delete(String keyValue) {
        this.deleteHandler.delete(keyValue);
        this.inquireAndCountCache.removeCache();
    }

    @Override
    public void batchDelete(List<String> keyValues) {
        this.deleteHandler.batchDelete(keyValues);
        this.inquireAndCountCache.removeCache();
    }
}
