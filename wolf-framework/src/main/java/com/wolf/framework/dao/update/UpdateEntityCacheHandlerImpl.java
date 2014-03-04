package com.wolf.framework.dao.update;

import com.wolf.framework.dao.parser.ColumnHandler;
import java.util.List;
import java.util.Map;
import net.sf.ehcache.Cache;

/**
 *
 * @author aladdin
 */
public class UpdateEntityCacheHandlerImpl implements UpdateHandler {

    private final Cache entityCache;
    private final ColumnHandler keyHandler;
    private final UpdateHandler updateHandler;

    public UpdateEntityCacheHandlerImpl(Cache entityCache, ColumnHandler keyHandler, UpdateHandler updateHandler) {
        this.entityCache = entityCache;
        this.keyHandler = keyHandler;
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
        String keyName = this.keyHandler.getColumnName();
        String keyValue;
        for (Map<String, String> entityMap : entityMapList) {
            keyValue = entityMap.get(keyName);
            this.entityCache.removeQuiet(keyValue);
        }
    }
}
