package com.wolf.framework.dao.cassandra;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.wolf.framework.dao.ColumnHandler;
import com.wolf.framework.dao.Entity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author jianying9
 * @param <T>
 */
public class CEntityDaoImpl<T extends Entity> extends AbstractCDao<T> implements CEntityDao<T> {

    private final PreparedStatement insertPs;
    private final PreparedStatement countPs;
    private final Map<String, String> setNames;
    private final Map<String, String> listNames;
    private final Map<String, String> mapNames;

    public CEntityDaoImpl(
            Session session,
            String keyspace,
            String table,
            List<ColumnHandler> keyHandlerList,
            List<ColumnHandler> columnHandlerList,
            Map<String, String> setNames,
            Map<String, String> listNames,
            Map<String, String> mapNames,
            Class<T> clazz) {
        super(session, keyspace, table, keyHandlerList, columnHandlerList, clazz);
        this.setNames = setNames;
        this.listNames = listNames;
        this.mapNames = mapNames;
        StringBuilder cqlBuilder = new StringBuilder(128);
        // insert
        cqlBuilder.append("INSERT INTO ").append(this.keyspace).append('.')
                .append(this.table).append('(');
        for (ColumnHandler ch : this.keyHandlerList) {
            cqlBuilder.append(ch.getDataMap()).append(", ");
        }
        for (ColumnHandler ch : this.columnHandlerList) {
            cqlBuilder.append(ch.getDataMap()).append(", ");
        }
        cqlBuilder.setLength(cqlBuilder.length() - 2);
        cqlBuilder.append(") values (");
        long num = this.columnHandlerList.size() + this.keyHandlerList.size();
        while (num > 0) {
            cqlBuilder.append("?, ");
            num--;
        }
        cqlBuilder.setLength(cqlBuilder.length() - 2);
        cqlBuilder.append(");");
        String insertCql = cqlBuilder.toString();
        cqlBuilder.setLength(0);
        this.insertPs = this.prepare(insertCql);
        this.logger.debug("{} insertCql:{}", this.table, insertCql);
        //count
        cqlBuilder.append("SELECT COUNT(*) FROM ").append(this.keyspace)
                .append('.').append(this.table).append(';');
        String countCql = cqlBuilder.toString();
        cqlBuilder.setLength(0);
        this.countPs = this.prepare(countCql);
        this.logger.debug("{} countCql:{}", this.table, countCql);
    }

    @Override
    public Object[] insert(Map<String, Object> entityMap) {
        List<Object> valueList = new ArrayList<>(this.columnHandlerList.size() + this.keyHandlerList.size());
        Object value;
        for (ColumnHandler ch : this.keyHandlerList) {
            value = entityMap.get(ch.getColumnName());
            if (value == null) {
                throw new RuntimeException("Can not find keyValue when insert:" + entityMap.toString());
            }
            valueList.add(value);
        }
        Object[] keyValue = valueList.toArray();
        for (ColumnHandler ch : this.columnHandlerList) {
            value = entityMap.get(ch.getColumnName());
            if (value == null) {
                value = ch.getDefaultValue();
            }
            valueList.add(value);
        }
        Object[] values = valueList.toArray();
        ResultSetFuture rsf = this.executeAsync(this.insertPs.bind(values));
        try {
            rsf.get();
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }
        return keyValue;
    }

    @Override
    public T insertAndInquire(Map<String, Object> entityMap) {
        Object[] keyValue = this.insert(entityMap);
        return this.inquireByKey(keyValue);
    }

    @Override
    public void batchInsert(List<Map<String, Object>> entityMapList) {
        if (entityMapList.isEmpty() == false) {
            Object value;
            List<Object> valueList = new ArrayList<>(this.columnHandlerList.size() + this.keyHandlerList.size());
            BatchStatement batch = new BatchStatement();
            boolean canInsert;
            Object[] values;
            for (Map<String, Object> entityMap : entityMapList) {
                canInsert = true;
                valueList.clear();
                for (ColumnHandler ch : this.keyHandlerList) {
                    value = entityMap.get(ch.getColumnName());
                    if (value == null) {
                        canInsert = false;
                        break;
                    }
                    valueList.add(value);
                }
                if (canInsert) {
                    for (ColumnHandler ch : this.columnHandlerList) {
                        value = entityMap.get(ch.getColumnName());
                        if (value == null) {
                            value = ch.getDefaultValue();
                        }
                        valueList.add(value);
                    }
                    values = valueList.toArray();
                    batch.add(this.insertPs.bind(values));
                }
            }
            ResultSetFuture rsf = this.executeAsync(batch);
            try {
                rsf.get();
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public Object[] update(Map<String, Object> entityMap) {
        List<Object> valueList = new ArrayList<>(this.columnHandlerList.size() + this.keyHandlerList.size());
        Object value;
        for (ColumnHandler ch : this.keyHandlerList) {
            value = entityMap.get(ch.getColumnName());
            if (value == null) {
                throw new RuntimeException("Can not find keyValue when update:" + entityMap.toString());
            }
            valueList.add(value);
        }
        Object[] keyValue = valueList.toArray();
        valueList.clear();
        StringBuilder cqlBuilder = new StringBuilder(128);
        boolean canUpdate = false;
        cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
                .append(this.table).append(" SET ");
        for (ColumnHandler ch : this.columnHandlerList) {
            value = entityMap.get(ch.getColumnName());
            if (value != null) {
                canUpdate = true;
                valueList.add(value);
                cqlBuilder.append(ch.getDataMap()).append(" = ?, ");
            }
        }
        cqlBuilder.setLength(cqlBuilder.length() - 2);
        if (canUpdate) {
            cqlBuilder.append(" WHERE ");
            for (ColumnHandler ch : this.keyHandlerList) {
                cqlBuilder.append(ch.getDataMap()).append(" = ? AND ");
                value = entityMap.get(ch.getColumnName());
                valueList.add(value);
            }
            cqlBuilder.setLength(cqlBuilder.length() - 4);
            cqlBuilder.append(";");
            Object[] values = valueList.toArray();
            String updateCql = cqlBuilder.toString();
            this.logger.debug("{} updateCql:{}", this.table, updateCql);
            PreparedStatement ps = this.cachePrepare(updateCql);
            ResultSetFuture rsf = this.executeAsync(ps.bind(values));
            try {
                rsf.get();
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }
        }
        return keyValue;
    }

    @Override
    public void batchUpdate(List<Map<String, Object>> entityMapList) {
        if (entityMapList.isEmpty() == false) {
            Object value;
            Object[] values;
            List<Object> valueList = new ArrayList<>(this.columnHandlerList.size() + 1);
            StringBuilder cqlBuilder = new StringBuilder(128);
            BatchStatement batch = new BatchStatement();
            boolean canUpdate;
            PreparedStatement ps;
            for (Map<String, Object> entityMap : entityMapList) {
                valueList.clear();
                canUpdate = false;
                cqlBuilder.setLength(0);
                cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
                        .append(this.table).append(" SET ");
                for (ColumnHandler ch : this.columnHandlerList) {
                    value = entityMap.get(ch.getColumnName());
                    if (value != null) {
                        canUpdate = true;
                        valueList.add(value);
                        cqlBuilder.append(ch.getDataMap()).append(" = ?, ");
                    }
                }
                cqlBuilder.setLength(cqlBuilder.length() - 2);
                cqlBuilder.append(" WHERE ");
                for (ColumnHandler ch : this.keyHandlerList) {
                    cqlBuilder.append(ch.getDataMap()).append(" = ? AND ");
                    value = entityMap.get(ch.getColumnName());
                    if (value == null) {
                        canUpdate = false;
                        break;
                    }
                    valueList.add(value);
                }
                cqlBuilder.setLength(cqlBuilder.length() - 4);
                cqlBuilder.append(" IF EXISTS;");
                if (canUpdate) {
                    ps = this.cachePrepare(cqlBuilder.toString());
                    values = valueList.toArray();
                    batch.add(ps.bind(values));
                }
            }
            ResultSetFuture rsf = this.executeAsync(batch);
            try {
                rsf.get();
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public T updateAndInquire(Map<String, Object> entityMap) {
        Object[] keyValue = this.update(entityMap);
        return this.inquireByKey(keyValue);
    }

    @Override
    public void batchDelete(List<Object[]> keyValues) {
        if (keyValues.isEmpty() == false) {
            BatchStatement batch = new BatchStatement();
            for (Object[] keyValue : keyValues) {
                batch.add(this.deletePs.bind(keyValue));
            }
            ResultSetFuture rsf = this.executeAsync(batch);
            try {
                rsf.get();
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public long count() {
        long result = 0;
        ResultSetFuture rsf = this.executeAsync(this.countPs.bind());
        ResultSet rs;
        Row r = null;
        try {
            rs = rsf.get();
            r = rs.one();
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }
        if (r != null) {
            result = r.getLong(0);
        }
        return result;
    }

    @Override
    public ResultSet execute(String cql, Object... values) {
        PreparedStatement ps = this.cachePrepare(cql);
        ResultSetFuture rsf = this.executeAsync(ps.bind(values));
        ResultSet rs;
        try {
            rs = rsf.get();
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }
        return rs;
    }

    @Override
    public List<T> query(String cql, Object... values) {
        List<T> resultList = Collections.EMPTY_LIST;
        PreparedStatement ps = this.cachePrepare(cql);
        ResultSetFuture rsf = this.executeAsync(ps.bind(values));
        ResultSet rs;
        try {
            rs = rsf.get();
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }
        List<Row> rList = rs.all();
        if (rList.isEmpty() == false) {
            resultList = new ArrayList<>(rList.size());
            Map<String, Object> map;
            T t;
            for (Row r : rList) {
                map = this.parseRow(r);
                t = this.parseMap(map);
                resultList.add(t);
            }
        }
        return resultList;
    }

    @Override
    public <S extends Object> void addSet(String columnName, S columnValue, Object... keyValue) {
        Set<S> set = new HashSet<>(2, 1);
        set.add(columnValue);
        this.addSet(columnName, set, keyValue);
    }

    @Override
    public <S extends Object> void addSet(String columnName, Set<S> columnValues, Object... keyValue) {
        String dataMap = this.setNames.get(columnName);
        if (dataMap != null) {
            List<Object> valueList = new ArrayList<>(this.keyHandlerList.size() + 1);
            valueList.add(columnValues);
            valueList.addAll(Arrays.asList(keyValue));
            StringBuilder cqlBuilder = new StringBuilder(128);
            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
                    .append(this.table).append(" SET ").append(dataMap)
                    .append(" = ").append(dataMap).append(" + ? WHERE ");
            for (ColumnHandler ch : this.keyHandlerList) {
                cqlBuilder.append(ch.getDataMap()).append(" = ? AND ");
            }
            cqlBuilder.setLength(cqlBuilder.length() - 4);
            cqlBuilder.append(";");
            PreparedStatement ps = this.cachePrepare(cqlBuilder.toString());
            ResultSetFuture rsf = this.executeAsync(ps.bind(valueList.toArray()));
            try {
                rsf.get();
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public <S extends Object> void removeSet(String columnName, S columnValue, Object... keyValue) {
        Set<S> set = new HashSet<>(2, 1);
        set.add(columnValue);
        this.removeSet(columnName, set, keyValue);
    }

    @Override
    public <S extends Object> void removeSet(String columnName, Set<S> columnValues, Object... keyValue) {
        String dataMap = this.setNames.get(columnName);
        if (dataMap != null) {
            List<Object> valueList = new ArrayList<>(this.keyHandlerList.size() + 1);
            valueList.add(columnValues);
            valueList.addAll(Arrays.asList(keyValue));
            StringBuilder cqlBuilder = new StringBuilder(128);
            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
                    .append(this.table).append(" SET ").append(dataMap)
                    .append(" = ").append(dataMap).append(" - ? WHERE ");
            for (ColumnHandler ch : this.keyHandlerList) {
                cqlBuilder.append(ch.getDataMap()).append(" = ? AND ");
            }
            cqlBuilder.setLength(cqlBuilder.length() - 4);
            cqlBuilder.append(";");
            PreparedStatement ps = this.cachePrepare(cqlBuilder.toString());
            ResultSetFuture rsf = this.executeAsync(ps.bind(valueList.toArray()));
            try {
                rsf.get();
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void clearSet(String columnName, Object... keyValue) {
        String dataMap = this.setNames.get(columnName);
        if (dataMap != null) {
            StringBuilder cqlBuilder = new StringBuilder(128);
            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
                    .append(this.table).append(" SET ").append(dataMap)
                    .append(" = {} WHERE ");
            for (ColumnHandler ch : this.keyHandlerList) {
                cqlBuilder.append(ch.getDataMap()).append(" = ? AND ");
            }
            cqlBuilder.setLength(cqlBuilder.length() - 4);
            cqlBuilder.append(";");
            PreparedStatement ps = this.cachePrepare(cqlBuilder.toString());
            ResultSetFuture rsf = this.executeAsync(ps.bind(keyValue));
            try {
                rsf.get();
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public <S extends Object> Set<S> getSet(String columnName, Class<S> type, Object... keyValue) {
        Set<S> result = Collections.EMPTY_SET;
        String dataMap = this.setNames.get(columnName);
        if (dataMap != null) {
            StringBuilder cqlBuilder = new StringBuilder(128);
            cqlBuilder.append("SELECT ").append(dataMap).append(" FROM ")
                    .append(this.keyspace).append('.').append(this.table)
                    .append(" WHERE ");
            for (ColumnHandler ch : this.keyHandlerList) {
                cqlBuilder.append(ch.getDataMap()).append(" = ? AND ");
            }
            cqlBuilder.setLength(cqlBuilder.length() - 4);
            cqlBuilder.append(";");
            PreparedStatement ps = this.cachePrepare(cqlBuilder.toString());
            ResultSetFuture rsf = this.executeAsync(ps.bind(keyValue));
            ResultSet rs;
            Row r = null;
            try {
                rs = rsf.get();
                r = rs.one();
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }
            if (r != null) {
                result = r.getSet(0, type);
            }
        }
        return result;
    }

    @Override
    public <L extends Object> void addList(String columnName, L columnValue, Object... keyValue) {
        List<L> list = new ArrayList<>(1);
        list.add(columnValue);
        this.addList(columnName, list, keyValue);
    }

    @Override
    public <L extends Object> void addList(String columnName, List<L> columnValues, Object... keyValue) {
        String dataMap = this.listNames.get(columnName);
        if (dataMap != null) {
            List<Object> valueList = new ArrayList<>(this.keyHandlerList.size() + 1);
            valueList.add(columnValues);
            valueList.addAll(Arrays.asList(keyValue));
            StringBuilder cqlBuilder = new StringBuilder(128);
            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
                    .append(this.table).append(" SET ").append(dataMap)
                    .append(" = ").append(dataMap).append(" + ? WHERE ");
            for (ColumnHandler ch : this.keyHandlerList) {
                cqlBuilder.append(ch.getDataMap()).append(" = ? AND ");
            }
            cqlBuilder.setLength(cqlBuilder.length() - 4);
            cqlBuilder.append(";");
            PreparedStatement ps = this.cachePrepare(cqlBuilder.toString());
            ResultSetFuture rsf = this.executeAsync(ps.bind(valueList.toArray()));
            try {
                rsf.get();
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public <L extends Object> void addFirstList(String columnName, L columnValue, Object... keyValue) {
        List<L> list = new ArrayList<>(1);
        list.add(columnValue);
        this.addFirstList(columnName, list, keyValue);
    }

    @Override
    public <L extends Object> void addFirstList(String columnName, List<L> columnValues, Object... keyValue) {
        String dataMap = this.listNames.get(columnName);
        if (dataMap != null) {
            List<Object> valueList = new ArrayList<>(this.keyHandlerList.size() + 1);
            valueList.add(columnValues);
            valueList.addAll(Arrays.asList(keyValue));
            StringBuilder cqlBuilder = new StringBuilder(128);
            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
                    .append(this.table).append(" SET ").append(dataMap)
                    .append(" = ").append("? + ").append(dataMap).append(" WHERE ");
            for (ColumnHandler ch : this.keyHandlerList) {
                cqlBuilder.append(ch.getDataMap()).append(" = ? AND ");
            }
            cqlBuilder.setLength(cqlBuilder.length() - 4);
            cqlBuilder.append(";");
            PreparedStatement ps = this.cachePrepare(cqlBuilder.toString());
            ResultSetFuture rsf = this.executeAsync(ps.bind(valueList.toArray()));
            try {
                rsf.get();
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public <L extends Object> void removeList(String columnName, L columnValue, Object... keyValue) {
        List<L> list = new ArrayList<>(1);
        list.add(columnValue);
        this.removeList(columnName, list, keyValue);
    }

    @Override
    public <L extends Object> void removeList(String columnName, List<L> columnValues, Object... keyValue) {
        String dataMap = this.listNames.get(columnName);
        if (dataMap != null) {
            List<Object> valueList = new ArrayList<>(this.keyHandlerList.size() + 1);
            valueList.add(columnValues);
            valueList.addAll(Arrays.asList(keyValue));
            StringBuilder cqlBuilder = new StringBuilder(128);
            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
                    .append(this.table).append(" SET ").append(dataMap)
                    .append(" = ").append(dataMap).append(" - ? WHERE ");
            for (ColumnHandler ch : this.keyHandlerList) {
                cqlBuilder.append(ch.getDataMap()).append(" = ? AND ");
            }
            cqlBuilder.setLength(cqlBuilder.length() - 4);
            cqlBuilder.append(";");
            PreparedStatement ps = this.cachePrepare(cqlBuilder.toString());
            ResultSetFuture rsf = this.executeAsync(ps.bind(valueList.toArray()));
            try {
                rsf.get();
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void clearList(String columnName, Object... keyValue) {
        String dataMap = this.listNames.get(columnName);
        if (dataMap != null) {
            StringBuilder cqlBuilder = new StringBuilder(128);
            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
                    .append(this.table).append(" SET ").append(dataMap)
                    .append(" = [] WHERE ");
            for (ColumnHandler ch : this.keyHandlerList) {
                cqlBuilder.append(ch.getDataMap()).append(" = ? AND ");
            }
            cqlBuilder.setLength(cqlBuilder.length() - 4);
            cqlBuilder.append(";");
            PreparedStatement ps = this.cachePrepare(cqlBuilder.toString());
            ResultSetFuture rsf = this.executeAsync(ps.bind(keyValue));
            try {
                rsf.get();
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public <L extends Object> List<L> getList(String columnName, Class<L> type, Object... keyValue) {
        List<L> result = Collections.EMPTY_LIST;
        String dataMap = this.listNames.get(columnName);
        if (dataMap != null) {
            StringBuilder cqlBuilder = new StringBuilder(128);
            cqlBuilder.append("SELECT ").append(dataMap).append(" FROM ")
                    .append(this.keyspace).append('.').append(this.table)
                    .append(" WHERE ");
            for (ColumnHandler ch : this.keyHandlerList) {
                cqlBuilder.append(ch.getDataMap()).append(" = ? AND ");
            }
            cqlBuilder.setLength(cqlBuilder.length() - 4);
            cqlBuilder.append(";");
            PreparedStatement ps = this.cachePrepare(cqlBuilder.toString());
            ResultSetFuture rsf = this.executeAsync(ps.bind(keyValue));
            ResultSet rs;
            Row r = null;
            try {
                rs = rsf.get();
                r = rs.one();
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }
            if (r != null) {
                result = r.getList(0, type);
            }
        }
        return result;
    }

    @Override
    public <K extends Object, V extends Object> void addMap(String columnName, K mapKeyValue, V mapValue, Object... keyValue) {
        Map<K, V> map = new HashMap<>(2, 1);
        map.put(mapKeyValue, mapValue);
        this.addMap(columnName, map, keyValue);
    }

    @Override
    public <K extends Object, V extends Object> void addMap(String columnName, Map<K, V> maps, Object... keyValue) {
        String dataMap = this.mapNames.get(columnName);
        if (dataMap != null) {
            List<Object> valueList = new ArrayList<>(this.keyHandlerList.size() + 1);
            valueList.add(maps);
            valueList.addAll(Arrays.asList(keyValue));
            StringBuilder cqlBuilder = new StringBuilder(128);
            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
                    .append(this.table).append(" SET ").append(dataMap)
                    .append(" = ").append(dataMap).append(" + ? WHERE ");
            for (ColumnHandler ch : this.keyHandlerList) {
                cqlBuilder.append(ch.getDataMap()).append(" = ? AND ");
            }
            cqlBuilder.setLength(cqlBuilder.length() - 4);
            cqlBuilder.append(";");
            PreparedStatement ps = this.cachePrepare(cqlBuilder.toString());
            ResultSetFuture rsf = this.executeAsync(ps.bind(valueList.toArray()));
            try {
                rsf.get();
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public <K extends Object, V extends Object> void removeMap(String columnName, K mapKeyValue, Object... keyValue) {
        String dataMap = this.mapNames.get(columnName);
        if (dataMap != null) {
            List<Object> valueList = new ArrayList<>(this.keyHandlerList.size() + 1);
            valueList.add(mapKeyValue);
            valueList.addAll(Arrays.asList(keyValue));
            StringBuilder cqlBuilder = new StringBuilder(128);
            cqlBuilder.append("DELETE ").append(dataMap).append("[?] FROM ")
                    .append(this.keyspace).append('.').append(this.table).append(" WHERE ");
            for (ColumnHandler ch : this.keyHandlerList) {
                cqlBuilder.append(ch.getDataMap()).append(" = ? AND ");
            }
            cqlBuilder.setLength(cqlBuilder.length() - 4);
            cqlBuilder.append(";");
            PreparedStatement ps = this.cachePrepare(cqlBuilder.toString());
            ResultSetFuture rsf = this.executeAsync(ps.bind(valueList.toArray()));
            try {
                rsf.get();
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public <K extends Object, V extends Object> void removeMap(String columnName, List<K> mapKeyValues, Object... keyValue) {
        String dataMap = this.mapNames.get(columnName);
        if (dataMap != null) {
            StringBuilder cqlBuilder = new StringBuilder(128);
            cqlBuilder.append("DELETE ").append(dataMap).append("[?] FROM ")
                    .append(this.keyspace).append('.').append(this.table).append(" WHERE ");
            for (ColumnHandler ch : this.keyHandlerList) {
                cqlBuilder.append(ch.getDataMap()).append(" = ? AND ");
            }
            cqlBuilder.setLength(cqlBuilder.length() - 4);
            cqlBuilder.append(";");
            PreparedStatement ps = this.cachePrepare(cqlBuilder.toString());
            BatchStatement batch = new BatchStatement();
            List<Object> valueList = new ArrayList<>(this.keyHandlerList.size() + 1);
            for (K mapKeyValue : mapKeyValues) {
                valueList.add(mapKeyValue);
                valueList.addAll(Arrays.asList(keyValue));
                batch.add(ps.bind(valueList.toArray()));
                valueList.clear();
            }
            ResultSetFuture rsf = this.executeAsync(batch);
            try {
                rsf.get();
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void clearMap(String columnName, Object... keyValue) {
        String dataMap = this.mapNames.get(columnName);
        if (dataMap != null) {
            StringBuilder cqlBuilder = new StringBuilder(128);
            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
                    .append(this.table).append(" SET ").append(dataMap)
                    .append(" = {} WHERE ");
            for (ColumnHandler ch : this.keyHandlerList) {
                cqlBuilder.append(ch.getDataMap()).append(" = ? AND ");
            }
            cqlBuilder.setLength(cqlBuilder.length() - 4);
            cqlBuilder.append(";");
            PreparedStatement ps = this.cachePrepare(cqlBuilder.toString());
            ResultSetFuture rsf = this.executeAsync(ps.bind(keyValue));
            try {
                rsf.get();
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public <K extends Object, V extends Object> Map<K, V> getMap(String columnName, Class<K> keyType, Class<V> valueType, Object... keyValue) {
        Map<K, V> result = Collections.EMPTY_MAP;
        String dataMap = this.mapNames.get(columnName);
        if (dataMap != null) {
            StringBuilder cqlBuilder = new StringBuilder(128);
            cqlBuilder.append("SELECT ").append(dataMap).append(" FROM ")
                    .append(this.keyspace).append('.').append(this.table)
                    .append(" WHERE ");
            for (ColumnHandler ch : this.keyHandlerList) {
                cqlBuilder.append(ch.getDataMap()).append(" = ? AND ");
            }
            cqlBuilder.setLength(cqlBuilder.length() - 4);
            cqlBuilder.append(";");
            PreparedStatement ps = this.cachePrepare(cqlBuilder.toString());
            ResultSetFuture rsf = this.executeAsync(ps.bind(keyValue));
            ResultSet rs;
            Row r = null;
            try {
                rs = rsf.get();
                r = rs.one();
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }
            if (r != null) {
                result = r.getMap(0, keyType, valueType);
            }
        }
        return result;
    }
}
