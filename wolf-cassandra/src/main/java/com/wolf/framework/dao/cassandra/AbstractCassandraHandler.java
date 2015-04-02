package com.wolf.framework.dao.cassandra;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.dao.ColumnHandler;
import com.wolf.framework.logger.LogFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;

/**
 *
 *
 * @author jianying9
 */
public class AbstractCassandraHandler {

    protected final Logger logger = LogFactory.getLogger(FrameworkLogger.DAO);
    protected final String keyspace;
    protected final String table;
    protected final Session session;
    protected final List<ColumnHandler> keyHandlerList;
    protected final List<ColumnHandler> columnHandlerList;
    private final String inquireByKeyCql;
    private final String deleteCql;
    private final String countCql;

    public AbstractCassandraHandler(
            Session session,
            String keyspace,
            String table,
            List<ColumnHandler> keyHandlerList,
            List<ColumnHandler> columnHandlerList
    ) {
        this.session = session;
        this.keyspace = keyspace;
        this.table = table;
        this.keyHandlerList = keyHandlerList;
        this.columnHandlerList = columnHandlerList;
        StringBuilder cqlBuilder = new StringBuilder(128);
        //inquire by key
        cqlBuilder.append("SELECT ");
        for (ColumnHandler columnHandler : this.keyHandlerList) {
            cqlBuilder.append(columnHandler.getDataMap()).append(", ");
        }
        for (ColumnHandler columnHandler : this.columnHandlerList) {
            cqlBuilder.append(columnHandler.getDataMap()).append(", ");
        }
        cqlBuilder.setLength(cqlBuilder.length() - 2);
        cqlBuilder.append(" FROM  ").append(this.keyspace)
                .append('.').append(this.table).append(" WHERE ");
        for (ColumnHandler columnHandler : this.keyHandlerList) {
            cqlBuilder.append(columnHandler.getDataMap()).append(" = ? AND ");
        }
        cqlBuilder.setLength(cqlBuilder.length() - 4);
        cqlBuilder.append(';');
        this.inquireByKeyCql = cqlBuilder.toString();
        cqlBuilder.setLength(0);
        this.logger.debug("{} inquireByKeyCql:{}", this.table, this.inquireByKeyCql);
        //delete
        cqlBuilder.append("DELETE FROM ").append(this.keyspace).append('.')
                .append(this.table).append(" WHERE ");
        for (ColumnHandler columnHandler : this.keyHandlerList) {
            cqlBuilder.append(columnHandler.getDataMap()).append(" = ? AND ");
        }
        cqlBuilder.setLength(cqlBuilder.length() - 4);
        cqlBuilder.append(';');
        this.deleteCql = cqlBuilder.toString();
        this.logger.debug("{} deleteCql:{}", this.table, this.deleteCql);
        cqlBuilder.setLength(0);
        //count
        cqlBuilder.append("SELECT COUNT(*) FROM ").append(this.keyspace)
                .append('.').append(this.table);
        this.countCql = cqlBuilder.toString();
        cqlBuilder.setLength(0);
        this.logger.debug("{} countCql:{}", this.table, this.countCql);
    }

    protected final Object getValue(Row row, ColumnHandler columnHander, int index) {
        Object result;
        switch (columnHander.getColumnDataType()) {
            case STRING:
                result = row.getString(index);
                break;
            case LONG:
                result = row.getLong(index);
                break;
            case INT:
                result = row.getInt(index);
                break;
            default:
                result = row.getBool(index);
        }
        return result;
    }

    public final boolean exist(Object... keyValues) {
        PreparedStatement ps = this.session.prepare(this.inquireByKeyCql);
        ResultSetFuture rsf = this.session.executeAsync(ps.bind(keyValues));
        ResultSet rs;
        Row r = null;
        try {
            rs = rsf.get();
            r = rs.one();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        } catch (ExecutionException ex) {
            throw new RuntimeException(ex);
        }
        return r != null;
    }

    public final Map<String, Object> inquireByKey(Object... keyValues) {
        Map<String, Object> result = null;
        PreparedStatement ps = this.session.prepare(this.inquireByKeyCql);
        ResultSetFuture rsf = this.session.executeAsync(ps.bind(keyValues));
        ResultSet rs;
        Row r = null;
        try {
            rs = rsf.get();
            r = rs.one();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        } catch (ExecutionException ex) {
            throw new RuntimeException(ex);
        }
        if (r != null) {
            Object value;
            ColumnHandler ch;
            result = new HashMap<String, Object>(this.columnHandlerList.size() + 1);
            int keySize = this.keyHandlerList.size();
            for (int index = 0; index < keySize; index++) {
                ch = this.keyHandlerList.get(index);
                value = this.getValue(r, ch, index);
                if (value == null || value.equals("null")) {
                    value = ch.getDefaultValue();
                }
                result.put(ch.getColumnName(), value);
            }
            for (int index = 0; index < this.columnHandlerList.size(); index++) {
                ch = this.columnHandlerList.get(index);
                value = this.getValue(r, ch, index + keySize);
                if (value == null || value.equals("null")) {
                    value = ch.getDefaultValue();
                }
                result.put(ch.getColumnName(), value);
            }
        }
        return result;
    }

    public final void delete(Object... keyValue) {
        PreparedStatement ps = this.session.prepare(this.deleteCql);
        ResultSetFuture rsf = this.session.executeAsync(ps.bind(keyValue));
        try {
            rsf.get();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        } catch (ExecutionException ex) {
            throw new RuntimeException(ex);
        }
    }

    public final void batchDelete(List<Object[]> keyValues) {
        if (keyValues.isEmpty() == false) {
            BatchStatement batch = new BatchStatement();
            PreparedStatement ps = this.session.prepare(this.deleteCql);
            for (Object[] keyValue : keyValues) {
                batch.add(ps.bind(keyValue));
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

    public final long count() {
        long result = 0;
        PreparedStatement ps = this.session.prepare(this.countCql);
        ResultSetFuture rsf = this.session.executeAsync(ps.bind());
        ResultSet rs;
        Row r = null;
        try {
            rs = rsf.get();
            r = rs.one();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        } catch (ExecutionException ex) {
            throw new RuntimeException(ex);
        }
        if (r != null) {
            result = r.getLong(0);
        }
        return result;
    }
}
