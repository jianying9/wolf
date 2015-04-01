package com.wolf.framework.dao.cassandra;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.wolf.framework.dao.ColumnHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * 普通表
 *
 * @author jianying9
 */
public class CassandraHandlerImpl extends AbstractCassandraHandler implements CassandraHandler {

    private final String insertCql;
    private final Map<String, String> sets;
    private final Map<String, String> lists;
    private final Map<String, String> maps;

    public CassandraHandlerImpl(
            Session session,
            String keyspace,
            String table,
            ColumnHandler keyColumnHandler,
            List<ColumnHandler> columnHandlerList,
            Map<String, String> sets,
            Map<String, String> lists,
            Map<String, String> maps
    ) {
        super(session, keyspace, table, keyColumnHandler, columnHandlerList);
        final String keyDataMap = this.keyColumnHandler.getDataMap();
        this.sets = sets;
        this.lists = lists;
        this.maps = maps;
        StringBuilder cqlBuilder = new StringBuilder(128);
        // insert
        cqlBuilder.append("INSERT INTO ").append(this.keyspace).append('.')
                .append(this.table).append('(').append(keyDataMap)
                .append(',');
        if (this.columnHandlerList.isEmpty() == false) {
            for (ColumnHandler ch : this.columnHandlerList) {
                cqlBuilder.append(ch.getDataMap()).append(',');
            }
            cqlBuilder.setLength(cqlBuilder.length() - 1);
        }
        cqlBuilder.append(") values(");
        long num = this.columnHandlerList.size() + 1;
        while (num > 0) {
            cqlBuilder.append("?,");
            num--;
        }
        cqlBuilder.setLength(cqlBuilder.length() - 1);
        cqlBuilder.append(") IF NOT EXISTS;");
        this.insertCql = cqlBuilder.toString();
        cqlBuilder.setLength(0);
        this.logger.debug("{} insertCql:{}", this.table, this.insertCql);
    }

    @Override
    public String insert(Map<String, String> entityMap) {
        final String keyColumnName = this.keyColumnHandler.getColumnName();
        String keyValue = entityMap.get(keyColumnName);
        if (keyValue == null) {
            throw new RuntimeException("Can not find keyValue when insert:" + entityMap.toString());
        }
        PreparedStatement ps = this.session.prepare(this.insertCql);
        List<String> valueList = new ArrayList<String>(this.columnHandlerList.size() + 1);
        valueList.add(keyValue);
        String value;
        for (ColumnHandler ch : this.columnHandlerList) {
            value = entityMap.get(ch.getColumnName());
            if (value == null) {
                value = "";
            }
            valueList.add(value);
        }
        ResultSetFuture rsf = this.session.executeAsync(ps.bind(valueList));
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
    public void batchInsert(List<Map<String, String>> entityMapList) {
        if (entityMapList.isEmpty() == false) {
            String keyValue;
            String value;
            final String keyColumnName = this.keyColumnHandler.getColumnName();
            List<String> valueList = new ArrayList<String>(this.columnHandlerList.size() + 1);
            PreparedStatement ps = this.session.prepare(this.insertCql);
            BatchStatement batch = new BatchStatement();
            for (Map<String, String> entityMap : entityMapList) {
                keyValue = entityMap.get(keyColumnName);
                if (keyValue != null) {
                    valueList.clear();
                    valueList.add(keyValue);
                    for (ColumnHandler ch : this.columnHandlerList) {
                        value = entityMap.get(ch.getColumnName());
                        if (value == null) {
                            value = "";
                        }
                        valueList.add(value);
                    }
                    batch.add(ps.bind(valueList));
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
    public String update(Map<String, String> entityMap) {
        final String keyColumnName = this.keyColumnHandler.getColumnName();
        String keyValue = entityMap.get(keyColumnName);
        if (keyValue == null) {
            throw new RuntimeException("Can not find keyValue when update:" + entityMap.toString());
        }
        StringBuilder cqlBuilder = new StringBuilder(128);
        List<String> valueList = new ArrayList<String>(this.columnHandlerList.size() + 1);
        String value;
        boolean hasUpdate = false;
        cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
                .append(this.table).append(" SET ");
        for (ColumnHandler ch : this.columnHandlerList) {
            value = entityMap.get(ch.getColumnName());
            if (value != null) {
                hasUpdate = true;
                valueList.add(value);
                cqlBuilder.append(ch.getDataMap()).append(" = ?, ");
            }
        }
        if (hasUpdate) {
            final String keyDataMap = this.keyColumnHandler.getDataMap();
            cqlBuilder.setLength(cqlBuilder.length() - 1);
            cqlBuilder.append(" WHERE ").append(keyDataMap).append("= ? IF EXISTS;");
            valueList.add(keyValue);
            String updateCql = cqlBuilder.toString();
            this.logger.debug("{} updateCql:{}", this.table, updateCql);
            PreparedStatement ps = this.session.prepare(updateCql);
            ResultSetFuture rsf = this.session.executeAsync(ps.bind(valueList));
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
    public void batchUpdate(List<Map<String, String>> entityMapList) {
        if (entityMapList.isEmpty() == false) {
            final String keyColumnName = this.keyColumnHandler.getColumnName();
            final String keyDataMap = this.keyColumnHandler.getDataMap();
            String keyValue;
            String value;
            List<String> valueList = new ArrayList<String>(this.columnHandlerList.size() + 1);
            StringBuilder cqlBuilder = new StringBuilder(128);
            BatchStatement batch = new BatchStatement();
            boolean hasUpdate = false;
            PreparedStatement ps;
            for (Map<String, String> entityMap : entityMapList) {
                keyValue = entityMap.get(keyColumnName);
                if (keyValue != null) {
                    valueList.clear();
                    cqlBuilder.setLength(0);
                    cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
                            .append(this.table).append(" SET ");
                    for (ColumnHandler ch : this.columnHandlerList) {
                        value = entityMap.get(ch.getColumnName());
                        if (value != null) {
                            hasUpdate = true;
                            valueList.add(value);
                            cqlBuilder.append(ch.getDataMap()).append(" = ?, ");
                        }
                    }
                }
                if (hasUpdate) {
                    cqlBuilder.setLength(cqlBuilder.length() - 1);
                    cqlBuilder.append(" WHERE ").append(keyDataMap).append("= ? IF EXISTS;");
                    valueList.add(keyValue);
                    ps = this.session.prepare(cqlBuilder.toString());
                    batch.add(ps.bind(valueList));
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
    public void addSet(String keyValue, String columnName, String value) {
        Set<String> set = new HashSet<String>(2, 1);
        set.add(value);
        this.addSet(keyValue, columnName, set);
    }

    @Override
    public void addSet(String keyValue, String columnName, Set<String> values) {
        String dataMap = this.sets.get(columnName);
        if (dataMap != null) {
            final String keyDataMap = this.keyColumnHandler.getDataMap();
            StringBuilder cqlBuilder = new StringBuilder(128);
            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
                    .append(this.table).append(" SET ").append(dataMap)
                    .append(" = ").append(dataMap).append(" + ? WHERE ")
                    .append(keyDataMap).append(" = ?;");
            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
            ResultSetFuture rsf = this.session.executeAsync(ps.bind(values, keyValue));
            try {
                rsf.get();
            } catch (InterruptedException ex) {
            } catch (ExecutionException ex) {
            }
        }
    }

    @Override
    public void removeSet(String keyValue, String columnName, String value) {
        Set<String> set = new HashSet<String>(2, 1);
        set.add(value);
        this.removeSet(keyValue, columnName, set);
    }

    @Override
    public void removeSet(String keyValue, String columnName, Set<String> values) {
        String dataMap = this.sets.get(columnName);
        if (dataMap != null) {
            final String keyDataMap = this.keyColumnHandler.getDataMap();
            StringBuilder cqlBuilder = new StringBuilder(128);
            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
                    .append(this.table).append(" SET ").append(dataMap)
                    .append(" = ").append(dataMap).append(" - ? WHERE ")
                    .append(keyDataMap).append(" = ?;");
            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
            ResultSetFuture rsf = this.session.executeAsync(ps.bind(values, keyValue));
            try {
                rsf.get();
            } catch (InterruptedException ex) {
            } catch (ExecutionException ex) {
            }
        }
    }

    @Override
    public void clearSet(String keyValue, String columnName) {
        String dataMap = this.sets.get(columnName);
        if (dataMap != null) {
            final String keyDataMap = this.keyColumnHandler.getDataMap();
            StringBuilder cqlBuilder = new StringBuilder(128);
            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
                    .append(this.table).append(" SET ").append(dataMap)
                    .append(" = {} WHERE ").append(keyDataMap).append(" = ?;");
            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
            ResultSetFuture rsf = this.session.executeAsync(ps.bind(keyValue));
            try {
                rsf.get();
            } catch (InterruptedException ex) {
            } catch (ExecutionException ex) {
            }
        }
    }

    @Override
    public Set<String> getSet(String keyValue, String columnName) {
        Set<String> result = Collections.EMPTY_SET;
        String dataMap = this.sets.get(columnName);
        if (dataMap != null) {
            final String keyDataMap = this.keyColumnHandler.getDataMap();
            StringBuilder cqlBuilder = new StringBuilder(128);
            cqlBuilder.append("SELECT ").append(dataMap).append(" FROM ")
                    .append(this.keyspace).append('.').append(this.table)
                    .append(" WHERE ").append(keyDataMap).append(" = ?;");
            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
            ResultSetFuture rsf = this.session.executeAsync(ps.bind(keyValue));
            ResultSet rs;
            Row r = null;
            try {
                rs = rsf.get();
                r = rs.one();
            } catch (InterruptedException ex) {
            } catch (ExecutionException ex) {
            }
            if (r != null) {
                result = r.getSet(0, String.class);
            }
        }
        return result;
    }

    @Override
    public void addList(String keyValue, String columnName, String value) {
        List<String> list = new ArrayList<String>(1);
        list.add(value);
        this.addList(keyValue, columnName, list);
    }

    @Override
    public void addList(String keyValue, String columnName, List<String> values) {
        String dataMap = this.lists.get(columnName);
        if (dataMap != null) {
            final String keyDataMap = this.keyColumnHandler.getDataMap();
            StringBuilder cqlBuilder = new StringBuilder(128);
            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
                    .append(this.table).append(" SET ").append(dataMap)
                    .append(" = ").append(dataMap).append(" + ? WHERE ")
                    .append(keyDataMap).append(" = ?;");
            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
            ResultSetFuture rsf = this.session.executeAsync(ps.bind(values, keyValue));
            try {
                rsf.get();
            } catch (InterruptedException ex) {
            } catch (ExecutionException ex) {
            }
        }
    }

    @Override
    public void addFirstList(String keyValue, String columnName, String value) {
        List<String> list = new ArrayList<String>(1);
        list.add(value);
        this.addFirstList(keyValue, columnName, list);
    }

    @Override
    public void addFirstList(String keyValue, String columnName, List<String> values) {
        String dataMap = this.lists.get(columnName);
        if (dataMap != null) {
            final String keyDataMap = this.keyColumnHandler.getDataMap();
            StringBuilder cqlBuilder = new StringBuilder(128);
            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
                    .append(this.table).append(" SET ").append(dataMap)
                    .append(" = ").append("? + ").append(dataMap).append(" WHERE ")
                    .append(keyDataMap).append(" = ?;");
            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
            ResultSetFuture rsf = this.session.executeAsync(ps.bind(values, keyValue));
            try {
                rsf.get();
            } catch (InterruptedException ex) {
            } catch (ExecutionException ex) {
            }
        }
    }

    @Override
    public void removeList(String keyValue, String columnName, String value) {
        List<String> list = new ArrayList<String>(1);
        list.add(value);
        this.removeList(keyValue, columnName, list);
    }

    @Override
    public void removeList(String keyValue, String columnName, List<String> values) {
        String dataMap = this.lists.get(columnName);
        if (dataMap != null) {
            final String keyDataMap = this.keyColumnHandler.getDataMap();
            StringBuilder cqlBuilder = new StringBuilder(128);
            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
                    .append(this.table).append(" SET ").append(dataMap)
                    .append(" = ").append(dataMap).append(" - ? WHERE ")
                    .append(keyDataMap).append(" = ?;");
            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
            ResultSetFuture rsf = this.session.executeAsync(ps.bind(values, keyValue));
            try {
                rsf.get();
            } catch (InterruptedException ex) {
            } catch (ExecutionException ex) {
            }
        }
    }

    @Override
    public void clearList(String keyValue, String columnName) {
        String dataMap = this.lists.get(columnName);
        if (dataMap != null) {
            final String keyDataMap = this.keyColumnHandler.getDataMap();
            StringBuilder cqlBuilder = new StringBuilder(128);
            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
                    .append(this.table).append(" SET ").append(dataMap)
                    .append(" = [] WHERE ").append(keyDataMap).append(" = ?;");
            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
            ResultSetFuture rsf = this.session.executeAsync(ps.bind(keyValue));
            try {
                rsf.get();
            } catch (InterruptedException ex) {
            } catch (ExecutionException ex) {
            }
        }
    }

    @Override
    public List<String> getList(String keyValue, String columnName) {
        List<String> result = Collections.EMPTY_LIST;
        String dataMap = this.lists.get(columnName);
        if (dataMap != null) {
            final String keyDataMap = this.keyColumnHandler.getDataMap();
            StringBuilder cqlBuilder = new StringBuilder(128);
            cqlBuilder.append("SELECT ").append(dataMap).append(" FROM ")
                    .append(this.keyspace).append('.').append(this.table)
                    .append(" WHERE ").append(keyDataMap).append(" = ?;");
            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
            ResultSetFuture rsf = this.session.executeAsync(ps.bind(keyValue));
            ResultSet rs;
            Row r = null;
            try {
                rs = rsf.get();
                r = rs.one();
            } catch (InterruptedException ex) {
            } catch (ExecutionException ex) {
            }
            if (r != null) {
                result = r.getList(0, String.class);
            }
        }
        return result;
    }

    @Override
    public void addMap(String keyValue, String columnName, String mapKeyValue, String mapValue) {
        Map<String, String> map = new HashMap<String, String>(2, 1);
        map.put(mapKeyValue, mapValue);
        this.addMap(keyValue, columnName, map);
    }

    @Override
    public void addMap(String keyValue, String columnName, Map<String, String> values) {
        String dataMap = this.maps.get(columnName);
        if (dataMap != null) {
            final String keyDataMap = this.keyColumnHandler.getDataMap();
            StringBuilder cqlBuilder = new StringBuilder(128);
            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
                    .append(this.table).append(" SET ").append(dataMap)
                    .append(" = ").append(dataMap).append(" + ? WHERE ")
                    .append(keyDataMap).append(" = ?;");
            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
            ResultSetFuture rsf = this.session.executeAsync(ps.bind(values, keyValue));
            try {
                rsf.get();
            } catch (InterruptedException ex) {
            } catch (ExecutionException ex) {
            }
        }
    }

    @Override
    public void removeMap(String keyValue, String columnName, String mapKeyValue) {
        Set<String> set = new HashSet<String>(2, 1);
        set.add(mapKeyValue);
        this.removeMap(keyValue, columnName, set);
    }

    @Override
    public void removeMap(String keyValue, String columnName, Set<String> mapKeyValues) {
        String dataMap = this.maps.get(columnName);
        if (dataMap != null) {
            final String keyDataMap = this.keyColumnHandler.getDataMap();
            StringBuilder cqlBuilder = new StringBuilder(128);
            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
                    .append(this.table).append(" SET ").append(dataMap)
                    .append(" = ").append(dataMap).append(" - ? WHERE ")
                    .append(keyDataMap).append(" = ?;");
            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
            ResultSetFuture rsf = this.session.executeAsync(ps.bind(mapKeyValues, keyValue));
            try {
                rsf.get();
            } catch (InterruptedException ex) {
            } catch (ExecutionException ex) {
            }
        }
    }

    @Override
    public void clearMap(String keyValue, String columnName) {
        String dataMap = this.maps.get(columnName);
        if (dataMap != null) {
            final String keyDataMap = this.keyColumnHandler.getDataMap();
            StringBuilder cqlBuilder = new StringBuilder(128);
            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
                    .append(this.table).append(" SET ").append(dataMap)
                    .append(" = {} WHERE ").append(keyDataMap).append(" = ?;");
            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
            ResultSetFuture rsf = this.session.executeAsync(ps.bind(keyValue));
            try {
                rsf.get();
            } catch (InterruptedException ex) {
            } catch (ExecutionException ex) {
            }
        }
    }

    @Override
    public Map<String, String> getMap(String keyValue, String columnName) {
        Map<String, String> result = Collections.EMPTY_MAP;
        String dataMap = this.maps.get(columnName);
        if (dataMap != null) {
            final String keyDataMap = this.keyColumnHandler.getDataMap();
            StringBuilder cqlBuilder = new StringBuilder(128);
            cqlBuilder.append("SELECT ").append(dataMap).append(" FROM ")
                    .append(this.keyspace).append('.').append(this.table)
                    .append(" WHERE ").append(keyDataMap).append(" = ?;");
            PreparedStatement ps = this.session.prepare(cqlBuilder.toString());
            ResultSetFuture rsf = this.session.executeAsync(ps.bind(keyValue));
            ResultSet rs;
            Row r = null;
            try {
                rs = rsf.get();
                r = rs.one();
            } catch (InterruptedException ex) {
            } catch (ExecutionException ex) {
            }
            if (r != null) {
                result = r.getMap(0, String.class, String.class);
            }
        }
        return result;
    }

    @Override
    public long increase(String keyValue, String columnName, long value) {
        throw new RuntimeException("Not supported,counter table can use increase.");
    }
}
