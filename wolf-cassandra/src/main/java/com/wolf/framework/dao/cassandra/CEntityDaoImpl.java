package com.wolf.framework.dao.cassandra;

import com.wolf.framework.dao.Entity;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aladdin
 * @param <T>
 */
public class CEntityDaoImpl<T extends Entity> implements CEntityDao<T> {

    private final CassandraHandler cassandraHandler;

    public CEntityDaoImpl(CassandraHandler cassandraHandler) {
        this.cassandraHandler = cassandraHandler;
    }

    @Override
    public boolean exist(Object keyValue) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public T inquireByKey(Object keyValue) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<T> inquireByKeys(List<Object> keyValues) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String insert(Map<String, Object> entityMap) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public T insertAndInquire(Map<String, Object> entityMap) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void batchInsert(List<Map<String, Object>> entityMapList) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String update(Map<String, Object> entityMap) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void batchUpdate(List<Map<String, Object>> entityMapList) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public T updateAndInquire(Map<String, Object> entityMap) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(Object keyValue) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void batchDelete(List<Object> keyValues) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
