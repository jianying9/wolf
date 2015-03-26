package com.wolf.framework.dao.cassandra;

import com.wolf.framework.dao.Entity;
import com.wolf.framework.dao.condition.InquirePageContext;
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
public class CEntityDaoImpl<T extends Entity> implements CEntityDao<T> {

    private final InsertHandler insertHandler;
    private final UpdateHandler updateHandler;
    private final DeleteHandler deleteHandler;
    private final InquireByKeyHandler<T> inquireByKeyHandler;
    private final CassandraHandler cassandraHandler;

    public CEntityDaoImpl(InsertHandler insertHandler, UpdateHandler updateHandler, DeleteHandler deleteHandler, InquireByKeyHandler<T> inquireByKeyHandler, CassandraHandler cassandraHandler) {
        this.insertHandler = insertHandler;
        this.updateHandler = updateHandler;
        this.deleteHandler = deleteHandler;
        this.inquireByKeyHandler = inquireByKeyHandler;
        this.cassandraHandler = cassandraHandler;
    }

    @Override
    public boolean exist(String keyValue) {
        return false;
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
        return null;
    }

    @Override
    public List<String> inquireKeysDESC(InquirePageContext inquirePageContext) {
        return null;
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
        return 0;
    }
}
