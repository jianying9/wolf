package com.wolf.framework.dao.update;

import com.wolf.framework.dao.update.UpdateHandler;
import java.util.List;
import java.util.Map;
import net.sf.ehcache.Cache;

/**
 *
 * @author aladdin
 */
public class UpdateEntityCacheHandlerImpl implements UpdateHandler {

    private final Cache entityCache;
    private final String keyName;
    private final UpdateHandler updateHandler;

    public UpdateEntityCacheHandlerImpl(Cache entityCache, String keyName, UpdateHandler updateHandler) {
        this.entityCache = entityCache;
        this.keyName = keyName;
        this.updateHandler = updateHandler;
    }

    @Override
    public String update(Map<String, String> entityMap) {
        String keyValue = this.updateHandler.update(entityMap);
        this.entityCache.removeQuiet(keyValue);
        return keyValue;
    }

    @Override
    public void batchUpdate(List<Map<String, String>> entityMapList) {
        this.updateHandler.batchUpdate(entityMapList);
        String keyValue;
        for (Map<String, String> entityMap : entityMapList) {
            keyValue = entityMap.get(this.keyName);
            this.entityCache.removeQuiet(keyValue);
        }
    }
}
