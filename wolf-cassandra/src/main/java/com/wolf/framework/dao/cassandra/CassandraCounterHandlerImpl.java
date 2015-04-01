package com.wolf.framework.dao.cassandra;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.wolf.framework.dao.ColumnHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * counter表
 *
 * @author jianying9
 */
public class CassandraCounterHandlerImpl extends AbstractCassandraHandler implements CassandraHandler {

    public CassandraCounterHandlerImpl(
            Session session,
            String keyspace,
            String table,
            ColumnHandler keyColumnHandler,
            List<ColumnHandler> columnHandlerList
    ) {
        super(session, keyspace, table, keyColumnHandler, columnHandlerList);
    }

    @Override
    public String insert(Map<String, String> entityMap) {
        throw new RuntimeException("Not supported,counter table can not insert.");
    }

    @Override
    public void batchInsert(List<Map<String, String>> entityMapList) {
        throw new RuntimeException("Not supported,counter table can not batch insert.");
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
                cqlBuilder.append(ch.getDataMap()).append(" = ")
                        .append(ch.getDataMap()).append(" + ?,");
            }
        }
        if (hasUpdate) {
            final String keyDataMap = this.keyColumnHandler.getDataMap();
            cqlBuilder.setLength(cqlBuilder.length() - 1);
            cqlBuilder.append(" WHERE ").append(keyDataMap).append(" = ?;");
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
        throw new RuntimeException("Not supported,counter table can not batch update.");
    }

    @Override
    public void addSet(String keyValue, String columnName, String value) {
        throw new RuntimeException("Not supported,counter table can not use set.");
    }

    @Override
    public void addSet(String keyValue, String columnName, Set<String> values) {
        throw new RuntimeException("Not supported,counter table can not use set.");
    }

    @Override
    public void removeSet(String keyValue, String columnName, String value) {
        throw new RuntimeException("Not supported,counter table can not use set.");
    }

    @Override
    public void removeSet(String keyValue, String columnName, Set<String> values) {
        throw new RuntimeException("Not supported,counter table can not use set.");
    }

    @Override
    public void clearSet(String keyValue, String columnName) {
        throw new RuntimeException("Not supported,counter table can not use set.");
    }

    @Override
    public Set<String> getSet(String keyValue, String columnName) {
        throw new RuntimeException("Not supported,counter table can not use set.");
    }

    @Override
    public void addList(String keyValue, String columnName, String value) {
        throw new RuntimeException("Not supported,counter table can not use list.");
    }

    @Override
    public void addList(String keyValue, String columnName, List<String> values) {
        throw new RuntimeException("Not supported,counter table can not use list.");
    }

    @Override
    public void addFirstList(String keyValue, String columnName, String value) {
        throw new RuntimeException("Not supported,counter table can not use list.");
    }

    @Override
    public void addFirstList(String keyValue, String columnName, List<String> values) {
        throw new RuntimeException("Not supported,counter table can not use list.");
    }

    @Override
    public void removeList(String keyValue, String columnName, String value) {
        throw new RuntimeException("Not supported,counter table can not use list.");
    }

    @Override
    public void removeList(String keyValue, String columnName, List<String> values) {
        throw new RuntimeException("Not supported,counter table can not use list.");
    }

    @Override
    public void clearList(String keyValue, String columnName) {
        throw new RuntimeException("Not supported,counter table can not use list.");
    }

    @Override
    public List<String> getList(String keyValue, String columnName) {
        throw new RuntimeException("Not supported,counter table can not use list.");
    }

    @Override
    public void addMap(String keyValue, String columnName, String mapKeyValue, String mapValue) {
        throw new RuntimeException("Not supported,counter table can not use map.");
    }

    @Override
    public void addMap(String keyValue, String columnName, Map<String, String> values) {
        throw new RuntimeException("Not supported,counter table can not use map.");
    }

    @Override
    public void removeMap(String keyValue, String columnName, String mapKeyValue) {
        throw new RuntimeException("Not supported,counter table can not use map.");
    }

    @Override
    public void removeMap(String keyValue, String columnName, Set<String> mapKeyValues) {
        throw new RuntimeException("Not supported,counter table can not use map.");
    }

    @Override
    public void clearMap(String keyValue, String columnName) {
        throw new RuntimeException("Not supported,counter table can not use map.");
    }

    @Override
    public Map<String, String> getMap(String keyValue, String columnName) {
        throw new RuntimeException("Not supported,counter table can not use map.");
    }

    @Override
    public long increase(String keyValue, String columnName, long value) {
        long result = 0;
        ColumnHandler columnHandler = null;
        for (ColumnHandler ch : this.columnHandlerList) {
            if (ch.getColumnName().equals(columnName)) {
                columnHandler = ch;
                break;
            }
        }
        if (columnHandler != null) {
            final String keyDataMap = this.keyColumnHandler.getDataMap();
            final String columnDataMap = columnHandler.getDataMap();
            StringBuilder cqlBuilder = new StringBuilder(128);
            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
                    .append(this.table).append(" SET ").append(columnDataMap)
                    .append(" = ").append(columnDataMap).append(" + ? WHERE ")
                    .append(keyDataMap).append(" = ");
            cqlBuilder.setLength(cqlBuilder.length() - 1);
            cqlBuilder.append(" WHERE ").append(keyDataMap).append(" = ?;");
            String updateCql = cqlBuilder.toString();
            //
            List<String> valueList = new ArrayList<String>(2);
            valueList.add(Long.toString(value));
            valueList.add(keyValue);
            this.logger.debug("{} increase-updateCql:{}", this.table, updateCql);
            PreparedStatement updatePs = this.session.prepare(updateCql);
            BoundStatement update = updatePs.bind(valueList);
            //
            cqlBuilder.setLength(0);
            cqlBuilder.append("SELECT ").append(columnDataMap).append(" FROM ")
                    .append(this.keyspace).append('.').append(this.table)
                    .append(" WHERE ").append(keyDataMap).append(" = ?");
            String selectCql = cqlBuilder.toString();
            this.logger.debug("{} increase-selectCql:{}", this.table, selectCql);
            PreparedStatement selectPs = this.session.prepare(selectCql);
            BoundStatement select = selectPs.bind(keyValue);
            //同步
            Row r = null;
            synchronized (this) {
                ResultSetFuture rsf = this.session.executeAsync(update);
                try {
                    rsf.get();
                    rsf = this.session.executeAsync(select);
                    ResultSet rs  = rsf.get();
                    r = rs.one();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                } catch (ExecutionException ex) {
                    throw new RuntimeException(ex);
                }
            }
            if(r != null) {
                result = r.getLong(0);
            }
        }
        return result;
    }
}
