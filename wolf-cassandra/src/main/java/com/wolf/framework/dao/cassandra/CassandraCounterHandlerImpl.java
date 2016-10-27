package com.wolf.framework.dao.cassandra;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.wolf.framework.dao.ColumnHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * counter表
 *
 * @author jianying9
 */
public class CassandraCounterHandlerImpl extends AbstractCassandraHandler {

    public CassandraCounterHandlerImpl(
            Session session,
            String keyspace,
            String table,
            List<ColumnHandler> keyHandlerList,
            List<ColumnHandler> columnHandlerList
    ) {
        super(session, keyspace, table, keyHandlerList, columnHandlerList);
    }

    @Override
    public Object[] insert(Map<String, Object> entityMap) {
        throw new RuntimeException("Not supported,counter table can not insert.");
    }

    @Override
    public void batchInsert(List<Map<String, Object>> entityMapList) {
        throw new RuntimeException("Not supported,counter table can not batch insert.");
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
                cqlBuilder.append(ch.getDataMap()).append(" = ")
                        .append(ch.getDataMap()).append(" + ?, ");
            }
        }
        cqlBuilder.setLength(cqlBuilder.length() - 2);
        if (canUpdate) {
            cqlBuilder.append(" WHERE ");
            for (ColumnHandler ch : this.keyHandlerList) {
                cqlBuilder.append(ch.getDataMap()).append(" = ?, ");
                value = entityMap.get(ch.getColumnName());
                valueList.add(value);
            }
            cqlBuilder.setLength(cqlBuilder.length() - 2);
            cqlBuilder.append(';');
            Object[] values = valueList.toArray();
            String updateCql = cqlBuilder.toString();
            this.logger.debug("{} updateCql:{}", this.table, updateCql);
            PreparedStatement ps = this.session.prepare(updateCql);
            ResultSetFuture rsf = this.session.executeAsync(ps.bind(values));
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
        throw new RuntimeException("Not supported,counter table can not batch update.");
    }

    @Override
    public long increase(String columnName, long value, Object... keyValue) {
        long result = 0;
        ColumnHandler columnHandler = null;
        for (ColumnHandler ch : this.columnHandlerList) {
            if (ch.getColumnName().equals(columnName)) {
                columnHandler = ch;
                break;
            }
        }
        if (columnHandler != null) {
            final String columnDataMap = columnHandler.getDataMap();
            StringBuilder cqlBuilder = new StringBuilder(128);
            List<Object> valueList = new ArrayList<>(this.keyHandlerList.size() + 1);
            cqlBuilder.append("UPDATE ").append(this.keyspace).append('.')
                    .append(this.table).append(" SET ").append(columnDataMap)
                    .append(" = ").append(columnDataMap).append(" + ? WHERE ");
            valueList.add(value);
            for (ColumnHandler ch : this.keyHandlerList) {
                cqlBuilder.append(ch.getDataMap()).append(" = ?, ");
            }
            valueList.addAll(Arrays.asList(keyValue));
            cqlBuilder.setLength(cqlBuilder.length() - 2);
            cqlBuilder.append(';');
            String updateCql = cqlBuilder.toString();
            //
            Object[] values = valueList.toArray();
            this.logger.debug("{} increase-updateCql:{}", this.table, updateCql);
            PreparedStatement updatePs = this.session.prepare(updateCql);
            BoundStatement update = updatePs.bind(values);
            //
            cqlBuilder.setLength(0);
            valueList.clear();
            cqlBuilder.append("SELECT ").append(columnDataMap).append(" FROM ")
                    .append(this.keyspace).append('.').append(this.table)
                    .append(" WHERE ");
            for (ColumnHandler ch : this.keyHandlerList) {
                cqlBuilder.append(ch.getDataMap()).append(" = ? AND ");
            }
            cqlBuilder.setLength(cqlBuilder.length() - 4);
            cqlBuilder.append(';');
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
                    ResultSet rs = rsf.get();
                    r = rs.one();
                } catch (InterruptedException | ExecutionException ex) {
                    throw new RuntimeException(ex);
                }
            }
            if (r != null) {
                result = r.getLong(0);
            }
        }
        return result;
    }
}
