package com.wolf.framework.dao.cassandra;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Session;
import com.wolf.framework.dao.ColumnHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 普通表
 *
 * @author jianying9
 */
public class CassandraHandlerImpl extends AbstractCassandraHandler {

    private final String insertCql;
//    private final Map<String, String> sets;
//    private final Map<String, String> lists;
//    private final Map<String, String> maps;

    public CassandraHandlerImpl(
            Session session,
            String keyspace,
            String table,
            List<ColumnHandler> keyHandlerList,
            List<ColumnHandler> columnHandlerList
    ) {
        super(session, keyspace, table, keyHandlerList, columnHandlerList);
//        this.sets = sets;
//        this.lists = lists;
//        this.maps = maps;
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
        cqlBuilder.append(") IF NOT EXISTS;");
        this.insertCql = cqlBuilder.toString();
        cqlBuilder.setLength(0);
        this.logger.debug("{} insertCql:{}", this.table, this.insertCql);
    }

    @Override
    public Object[] insert(Map<String, Object> entityMap) {
        List<Object> valueList = new ArrayList<Object>(this.columnHandlerList.size() + this.keyHandlerList.size());
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
        PreparedStatement ps = this.session.prepare(this.insertCql);
        Object[] values = valueList.toArray();
        ResultSetFuture rsf = this.session.executeAsync(ps.bind(values));
        try {
            rsf.get();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        } catch (ExecutionException ex) {
            throw new RuntimeException(ex);
        }
        return keyValue;
    }

    @Override
    public void batchInsert(List<Map<String, Object>> entityMapList) {
        if (entityMapList.isEmpty() == false) {
            Object value;
            List<Object> valueList = new ArrayList<Object>(this.columnHandlerList.size() + this.keyHandlerList.size());
            PreparedStatement ps = this.session.prepare(this.insertCql);
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
                    batch.add(ps.bind(values));
                }
            }
            ResultSetFuture rsf = this.session.executeAsync(batch);
            try {
                rsf.get();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            } catch (ExecutionException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public Object[] update(Map<String, Object> entityMap) {
        List<Object> valueList = new ArrayList<Object>(this.columnHandlerList.size() + this.keyHandlerList.size());
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
            cqlBuilder.append(" IF EXISTS;");
            Object[] values = valueList.toArray();
            String updateCql = cqlBuilder.toString();
            this.logger.debug("{} updateCql:{}", this.table, updateCql);
            PreparedStatement ps = this.session.prepare(updateCql);
            ResultSetFuture rsf = this.session.executeAsync(ps.bind(values));
            try {
                rsf.get();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            } catch (ExecutionException ex) {
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
            List<Object> valueList = new ArrayList<Object>(this.columnHandlerList.size() + 1);
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
                    ps = this.session.prepare(cqlBuilder.toString());
                    values = valueList.toArray();
                    batch.add(ps.bind(values));
                }
            }
            ResultSetFuture rsf = this.session.executeAsync(batch);
            try {
                rsf.get();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            } catch (ExecutionException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public long increase(String columnName, long value, Object... keyValue) {
        throw new RuntimeException("Not supported,counter table can use increase.");
    }
    
//    public void addSet(String keyValue, String columnName, String value) {
//        Set<String> set = new HashSet<String>(2, 1);
//        set.add(value);
//        this.addSet(keyValue, columnName, set);
//    }
//
//    public void addSet(String keyValue, String columnName, Set<String> values) {
//        String dataMap = this.sets.get(columnName);
//        if (dataMap != null) {
//            final String keyDataMap = "";
//            StringBuilder cqlBuilder = new StringBuilder(128);
//            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
//                    .append(this.table).append(" SET ").append(dataMap)
//                    .append(" = ").append(dataMap).append(" + ? WHERE ")
//                    .append(keyDataMap).append(" = ?;");
//            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
//            ResultSetFuture rsf = this.session.executeAsync(ps.bind(values, keyValue));
//            try {
//                rsf.get();
//            } catch (InterruptedException ex) {
//            } catch (ExecutionException ex) {
//            }
//        }
//    }
    
//    public void removeSet(String keyValue, String columnName, String value) {
//        Set<String> set = new HashSet<String>(2, 1);
//        set.add(value);
//        this.removeSet(keyValue, columnName, set);
//    }
//
//    public void removeSet(String keyValue, String columnName, Set<String> values) {
//        String dataMap = this.sets.get(columnName);
//        if (dataMap != null) {
//            final String keyDataMap = "";
//            StringBuilder cqlBuilder = new StringBuilder(128);
//            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
//                    .append(this.table).append(" SET ").append(dataMap)
//                    .append(" = ").append(dataMap).append(" - ? WHERE ")
//                    .append(keyDataMap).append(" = ?;");
//            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
//            ResultSetFuture rsf = this.session.executeAsync(ps.bind(values, keyValue));
//            try {
//                rsf.get();
//            } catch (InterruptedException ex) {
//            } catch (ExecutionException ex) {
//            }
//        }
//    }

//    public void clearSet(String keyValue, String columnName) {
//        String dataMap = this.sets.get(columnName);
//        if (dataMap != null) {
//            final String keyDataMap = "";
//            StringBuilder cqlBuilder = new StringBuilder(128);
//            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
//                    .append(this.table).append(" SET ").append(dataMap)
//                    .append(" = {} WHERE ").append(keyDataMap).append(" = ?;");
//            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
//            ResultSetFuture rsf = this.session.executeAsync(ps.bind(keyValue));
//            try {
//                rsf.get();
//            } catch (InterruptedException ex) {
//            } catch (ExecutionException ex) {
//            }
//        }
//    }

//    public Set<String> getSet(String keyValue, String columnName) {
//        Set<String> result = Collections.EMPTY_SET;
//        String dataMap = this.sets.get(columnName);
//        if (dataMap != null) {
//            final String keyDataMap = "";
//            StringBuilder cqlBuilder = new StringBuilder(128);
//            cqlBuilder.append("SELECT ").append(dataMap).append(" FROM ")
//                    .append(this.keyspace).append('.').append(this.table)
//                    .append(" WHERE ").append(keyDataMap).append(" = ?;");
//            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
//            ResultSetFuture rsf = this.session.executeAsync(ps.bind(keyValue));
//            ResultSet rs;
//            Row r = null;
//            try {
//                rs = rsf.get();
//                r = rs.one();
//            } catch (InterruptedException ex) {
//            } catch (ExecutionException ex) {
//            }
//            if (r != null) {
//                result = r.getSet(0, String.class);
//            }
//        }
//        return result;
//    }
//    public void addList(String keyValue, String columnName, String value) {
//        List<String> list = new ArrayList<String>(1);
//        list.add(value);
//        this.addList(keyValue, columnName, list);
//    }
//
//    public void addList(String keyValue, String columnName, List<String> values) {
//        String dataMap = this.lists.get(columnName);
//        if (dataMap != null) {
//            final String keyDataMap = "";
//            StringBuilder cqlBuilder = new StringBuilder(128);
//            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
//                    .append(this.table).append(" SET ").append(dataMap)
//                    .append(" = ").append(dataMap).append(" + ? WHERE ")
//                    .append(keyDataMap).append(" = ?;");
//            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
//            ResultSetFuture rsf = this.session.executeAsync(ps.bind(values, keyValue));
//            try {
//                rsf.get();
//            } catch (InterruptedException ex) {
//            } catch (ExecutionException ex) {
//            }
//        }
//    }
//    public void addFirstList(String keyValue, String columnName, String value) {
//        List<String> list = new ArrayList<String>(1);
//        list.add(value);
//        this.addFirstList(keyValue, columnName, list);
//    }
//
//    public void addFirstList(String keyValue, String columnName, List<String> values) {
//        String dataMap = this.lists.get(columnName);
//        if (dataMap != null) {
//            final String keyDataMap = "";
//            StringBuilder cqlBuilder = new StringBuilder(128);
//            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
//                    .append(this.table).append(" SET ").append(dataMap)
//                    .append(" = ").append("? + ").append(dataMap).append(" WHERE ")
//                    .append(keyDataMap).append(" = ?;");
//            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
//            ResultSetFuture rsf = this.session.executeAsync(ps.bind(values, keyValue));
//            try {
//                rsf.get();
//            } catch (InterruptedException ex) {
//            } catch (ExecutionException ex) {
//            }
//        }
//    }
//    public void removeList(String keyValue, String columnName, String value) {
//        List<String> list = new ArrayList<String>(1);
//        list.add(value);
//        this.removeList(keyValue, columnName, list);
//    }
//
//    public void removeList(String keyValue, String columnName, List<String> values) {
//        String dataMap = this.lists.get(columnName);
//        if (dataMap != null) {
//            final String keyDataMap = "";
//            StringBuilder cqlBuilder = new StringBuilder(128);
//            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
//                    .append(this.table).append(" SET ").append(dataMap)
//                    .append(" = ").append(dataMap).append(" - ? WHERE ")
//                    .append(keyDataMap).append(" = ?;");
//            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
//            ResultSetFuture rsf = this.session.executeAsync(ps.bind(values, keyValue));
//            try {
//                rsf.get();
//            } catch (InterruptedException ex) {
//            } catch (ExecutionException ex) {
//            }
//        }
//    }
//    public void clearList(String keyValue, String columnName) {
//        String dataMap = this.lists.get(columnName);
//        if (dataMap != null) {
//            final String keyDataMap = "";
//            StringBuilder cqlBuilder = new StringBuilder(128);
//            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
//                    .append(this.table).append(" SET ").append(dataMap)
//                    .append(" = [] WHERE ").append(keyDataMap).append(" = ?;");
//            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
//            ResultSetFuture rsf = this.session.executeAsync(ps.bind(keyValue));
//            try {
//                rsf.get();
//            } catch (InterruptedException ex) {
//            } catch (ExecutionException ex) {
//            }
//        }
//    }
//    public List<String> getList(String keyValue, String columnName) {
//        List<String> result = Collections.EMPTY_LIST;
//        String dataMap = this.lists.get(columnName);
//        if (dataMap != null) {
//            final String keyDataMap = "";
//            StringBuilder cqlBuilder = new StringBuilder(128);
//            cqlBuilder.append("SELECT ").append(dataMap).append(" FROM ")
//                    .append(this.keyspace).append('.').append(this.table)
//                    .append(" WHERE ").append(keyDataMap).append(" = ?;");
//            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
//            ResultSetFuture rsf = this.session.executeAsync(ps.bind(keyValue));
//            ResultSet rs;
//            Row r = null;
//            try {
//                rs = rsf.get();
//                r = rs.one();
//            } catch (InterruptedException ex) {
//            } catch (ExecutionException ex) {
//            }
//            if (r != null) {
//                result = r.getList(0, String.class);
//            }
//        }
//        return result;
//    }
//    public void addMap(String keyValue, String columnName, String mapKeyValue, String mapValue) {
//        Map<String, String> map = new HashMap<String, String>(2, 1);
//        map.put(mapKeyValue, mapValue);
//        this.addMap(keyValue, columnName, map);
//    }
//    public void addMap(String keyValue, String columnName, Map<String, String> values) {
//        String dataMap = this.maps.get(columnName);
//        if (dataMap != null) {
//            final String keyDataMap = "";
//            StringBuilder cqlBuilder = new StringBuilder(128);
//            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
//                    .append(this.table).append(" SET ").append(dataMap)
//                    .append(" = ").append(dataMap).append(" + ? WHERE ")
//                    .append(keyDataMap).append(" = ?;");
//            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
//            ResultSetFuture rsf = this.session.executeAsync(ps.bind(values, keyValue));
//            try {
//                rsf.get();
//            } catch (InterruptedException ex) {
//            } catch (ExecutionException ex) {
//            }
//        }
//    }
//    public void removeMap(String keyValue, String columnName, String mapKeyValue) {
//        Set<String> set = new HashSet<String>(2, 1);
//        set.add(mapKeyValue);
//        this.removeMap(keyValue, columnName, set);
//    }
//    public void removeMap(String keyValue, String columnName, Set<String> mapKeyValues) {
//        String dataMap = this.maps.get(columnName);
//        if (dataMap != null) {
//            final String keyDataMap = "";
//            StringBuilder cqlBuilder = new StringBuilder(128);
//            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
//                    .append(this.table).append(" SET ").append(dataMap)
//                    .append(" = ").append(dataMap).append(" - ? WHERE ")
//                    .append(keyDataMap).append(" = ?;");
//            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
//            ResultSetFuture rsf = this.session.executeAsync(ps.bind(mapKeyValues, keyValue));
//            try {
//                rsf.get();
//            } catch (InterruptedException ex) {
//            } catch (ExecutionException ex) {
//            }
//        }
//    }
//    public void clearMap(String keyValue, String columnName) {
//        String dataMap = this.maps.get(columnName);
//        if (dataMap != null) {
//            final String keyDataMap = "";
//            StringBuilder cqlBuilder = new StringBuilder(128);
//            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
//                    .append(this.table).append(" SET ").append(dataMap)
//                    .append(" = {} WHERE ").append(keyDataMap).append(" = ?;");
//            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
//            ResultSetFuture rsf = this.session.executeAsync(ps.bind(keyValue));
//            try {
//                rsf.get();
//            } catch (InterruptedException ex) {
//            } catch (ExecutionException ex) {
//            }
//        }
//    }
//    public Map<String, String> getMap(String keyValue, String columnName) {
//        Map<String, String> result = Collections.EMPTY_MAP;
//        String dataMap = this.maps.get(columnName);
//        if (dataMap != null) {
//            final String keyDataMap = "";
//            StringBuilder cqlBuilder = new StringBuilder(128);
//            cqlBuilder.append("SELECT ").append(dataMap).append(" FROM ")
//                    .append(this.keyspace).append('.').append(this.table)
//                    .append(" WHERE ").append(keyDataMap).append(" = ?;");
//            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
//            ResultSetFuture rsf = this.session.executeAsync(ps.bind(keyValue));
//            ResultSet rs;
//            Row r = null;
//            try {
//                rs = rsf.get();
//                r = rs.one();
//            } catch (InterruptedException ex) {
//            } catch (ExecutionException ex) {
//            }
//            if (r != null) {
//                result = r.getMap(0, String.class, String.class);
//            }
//        }
//        return result;
//    }
}
