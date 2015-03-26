package com.wolf.framework.dao.reids;

import com.wolf.framework.dao.Entity;
import com.wolf.framework.dao.condition.InquirePageContext;
import com.wolf.framework.dao.condition.InquireIndexPageContext;
import com.wolf.framework.dao.delete.DeleteHandler;
import com.wolf.framework.dao.inquire.InquireByKeyHandler;
import com.wolf.framework.dao.insert.InsertHandler;
import com.wolf.framework.dao.update.UpdateHandler;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aladdin
 * @param <T>
 */
public class REntityDaoImpl<T extends Entity> implements REntityDao<T> {

    private final InsertHandler insertHandler;
    private final UpdateHandler updateHandler;
    private final DeleteHandler deleteHandler;
    private final InquireByKeyHandler<T> inquireByKeyHandler;
    private final RedisHandler redisHandler;

    public REntityDaoImpl(InsertHandler insertHandler, UpdateHandler updateHandler, DeleteHandler deleteHandler, InquireByKeyHandler<T> inquireByKeyHandler, RedisHandler redisHandler) {
        this.insertHandler = insertHandler;
        this.updateHandler = updateHandler;
        this.deleteHandler = deleteHandler;
        this.inquireByKeyHandler = inquireByKeyHandler;
        this.redisHandler = redisHandler;
    }

    @Override
    public boolean exist(String keyValue) {
        return this.redisHandler.exist(keyValue);
    }

    @Override
    public T inquireByKey(String keyValue) {
        return this.inquireByKeyHandler.inquireByKey(keyValue);
    }

    @Override
    public List<T> inquireByKeys(List<String> keyValues) {
        return this.inquireByKeyHandler.inquireByKeys(keyValues);
    }

    @Override
    public String insert(Map<String, String> entityMap) {
        return this.insertHandler.insert(entityMap);
    }

    @Override
    public T insertAndInquire(Map<String, String> entityMap) {
        String keyValue = this.insert(entityMap);
        return this.inquireByKey(keyValue);
    }

    @Override
    public void batchInsert(List<Map<String, String>> entityMapList) {
        int num = entityMapList.size();
        switch (num) {
            case 0:
                break;
            case 1:
                this.insertHandler.insert(entityMapList.get(0));
                break;
            default:
                this.insertHandler.batchInsert(entityMapList);
        }
    }

    @Override
    public String update(Map<String, String> entityMap) {
        return this.updateHandler.update(entityMap);
    }

    @Override
    public void batchUpdate(List<Map<String, String>> entityMapList) {
        int num = entityMapList.size();
        switch (num) {
            case 0:
                break;
            case 1:
                this.updateHandler.update(entityMapList.get(0));
                break;
            default:
                this.updateHandler.batchUpdate(entityMapList);
        }
    }

    @Override
    public T updateAndInquire(Map<String, String> entityMap) {
        String keyValue = this.updateHandler.update(entityMap);
        return this.inquireByKeyHandler.inquireByKey(keyValue);
    }

    @Override
    public void delete(String keyValue) {
        this.deleteHandler.delete(keyValue);
    }

    @Override
    public void batchDelete(List<String> keyValues) {
        int num = keyValues.size();
        switch (num) {
            case 0:
                break;
            case 1:
                this.deleteHandler.delete(keyValues.get(0));
                break;
            default:
                this.deleteHandler.batchDelete(keyValues);
        }
    }

    @Override
    public List<String> inquireKeys(InquirePageContext inquirePageContext) {
        return this.redisHandler.inquireKeys(inquirePageContext);
    }

    @Override
    public List<String> inquireKeysDESC(InquirePageContext inquirePageContext) {
        return this.redisHandler.inquireKeysDESC(inquirePageContext);
    }

    @Override
    public List<T> inquire(InquirePageContext inquirePageContext) {
        List<String> keyList = this.inquireKeys(inquirePageContext);
        return this.inquireByKeys(keyList);
    }

    @Override
    public List<T> inquireDESC(InquirePageContext inquirePageContext) {
        List<String> keyList = this.inquireKeysDESC(inquirePageContext);
        return this.inquireByKeys(keyList);
    }

    @Override
    public long count() {
        return this.redisHandler.count();
    }

    @Override
    public List<String> inquireKeysByIndex(InquireIndexPageContext inquireRedisIndexContext) {
        return this.redisHandler.inquireKeysByIndex(inquireRedisIndexContext);
    }

    @Override
    public List<String> inquireKeysByIndexDESC(InquireIndexPageContext inquireRedisIndexContext) {
        return this.redisHandler.inquireKeysByIndexDESC(inquireRedisIndexContext);
    }

    @Override
    public List<T> inquireByIndex(InquireIndexPageContext inquireRedisIndexContext) {
        List<String> keyList = this.inquireKeysByIndex(inquireRedisIndexContext);
        return this.inquireByKeys(keyList);
    }

    @Override
    public List<T> inquireByIndexDESC(InquireIndexPageContext inquireRedisIndexContext) {
        List<String> keyList = this.inquireKeysByIndexDESC(inquireRedisIndexContext);
        return this.inquireByKeys(keyList);
    }

    @Override
    public long countByIndex(String indexName, String indexValue) {
        return this.redisHandler.countByIndex(indexName, indexValue);
    }

    @Override
    public long increase(String keyValue, String columnName, long value) {
        return this.redisHandler.increase(keyValue, columnName, value);
    }

    @Override
    public void setKeySorce(Map<String, String> entityMap, long sorce) {
        String keyIndexName = this.redisHandler.getTableIndexKey();
        entityMap.put(keyIndexName, Long.toString(sorce));
    }

    @Override
    public void updateKeySorce(String keyValue, long sorce) {
        this.redisHandler.updateKeySorce(keyValue, sorce);
    }

    @Override
    public void setIndexSorce(Map<String, String> entityMap, String columnName, long sorce) {
        String columnValue = entityMap.get(columnName);
        if (columnValue != null) {
            String indexName = this.redisHandler.getColumnIndexKey(columnName, columnValue);
            entityMap.put(indexName, Long.toString(sorce));
        }
    }

    @Override
    public void updateIndexKeySorce(String keyValue, String columnName, String columnValue, long sorce) {
        this.redisHandler.updateIndexKeySorce(keyValue, columnName, columnValue, sorce);
    }

    @Override
    public void sortedSetAdd(String keyValue, String sortedSetName, String value, long score) {
        this.redisHandler.sortedSetAdd(keyValue, sortedSetName, value, score);
    }

    @Override
    public void sortedSetRemove(String keyValue, String sortedSetName, String value) {
        this.redisHandler.sortedSetRemove(keyValue, sortedSetName, value);
    }

    @Override
    public List<String> sortedSet(String keyValue, String sortedSetName) {
        return this.redisHandler.sortedSet(keyValue, sortedSetName);
    }

    @Override
    public List<String> sortedSetDESC(String keyValue, String sortedSetName) {
        return this.redisHandler.sortedSetDESC(keyValue, sortedSetName);
    }

    @Override
    public void sortedSetClear(String keyValue, String sortedSetName) {
        this.redisHandler.sortedSetClear(keyValue, sortedSetName);
    }
}
