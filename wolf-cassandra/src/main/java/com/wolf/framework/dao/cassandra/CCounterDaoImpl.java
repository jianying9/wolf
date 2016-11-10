package com.wolf.framework.dao.cassandra;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.wolf.framework.dao.ColumnHandler;
import com.wolf.framework.dao.Entity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author jianying9
 * @param <T>
 */
public class CCounterDaoImpl<T extends Entity> extends AbstractCDao<T> implements CCounterDao<T> {

    public CCounterDaoImpl(
            Session session,
            String keyspace,
            String table,
            List<ColumnHandler> keyHandlerList,
            List<ColumnHandler> columnHandlerList,
            Class<T> clazz) {
        super(session, keyspace, table, keyHandlerList, columnHandlerList, clazz);
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
            PreparedStatement updatePs = this.cachePrepare(updateCql);
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
            PreparedStatement selectPs = this.cachePrepare(selectCql);
            BoundStatement select = selectPs.bind(keyValue);
            //同步
            Row r = null;
            synchronized (this) {
                ResultSetFuture rsf = this.executeAsync(update);
                try {
                    rsf.get();
                    rsf = this.executeAsync(select);
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
