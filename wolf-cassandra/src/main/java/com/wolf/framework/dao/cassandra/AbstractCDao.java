package com.wolf.framework.dao.cassandra;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.exceptions.InvalidQueryException;
import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.dao.ColumnHandler;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.logger.LogFactory;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;

/**
 *
 * @author jianying9
 * @param <T>
 */
public abstract class AbstractCDao<T extends Entity> {

    protected final List<ColumnHandler> keyHandlerList;
    protected final List<ColumnHandler> columnHandlerList;
    protected final Class<T> clazz;
    protected final Logger logger = LogFactory.getLogger(FrameworkLogger.DAO);
    protected final String keyspace;
    protected final String table;
    private final Session session;
    private final PreparedStatement inquireByKeyPs;
    protected final PreparedStatement deletePs;
    private final Map<String, PreparedStatement> psCacheMap = new HashMap<>(2, 1);

    public AbstractCDao(
            Session session,
            String keyspace,
            String table,
            List<ColumnHandler> keyHandlerList,
            List<ColumnHandler> columnHandlerList,
            Class<T> clazz) {
        this.session = session;
        this.keyspace = keyspace;
        this.table = table;
        this.keyHandlerList = keyHandlerList;
        this.columnHandlerList = columnHandlerList;
        this.clazz = clazz;
        //
        StringBuilder cqlBuilder = new StringBuilder(128);
        //inquire by key
        cqlBuilder.append("SELECT * FROM ").append(this.keyspace)
                .append('.').append(this.table).append(" WHERE ");
        for (ColumnHandler columnHandler : this.keyHandlerList) {
            cqlBuilder.append(columnHandler.getDataMap()).append(" = ? AND ");
        }
        cqlBuilder.setLength(cqlBuilder.length() - 4);
        cqlBuilder.append(';');
        String inquireByKeyCql = cqlBuilder.toString();
        cqlBuilder.setLength(0);
        this.logger.debug("{} inquireByKeyCql:{}", this.table, inquireByKeyCql);
        try {
            this.inquireByKeyPs = this.session.prepare(inquireByKeyCql);
        } catch (InvalidQueryException e) {
            this.logger.error("{} inquireByKeyCql:{}", this.table, inquireByKeyCql);
            throw e;
        }
        //delete
        cqlBuilder.append("DELETE FROM ").append(this.keyspace).append('.')
                .append(this.table).append(" WHERE ");
        for (ColumnHandler columnHandler : this.keyHandlerList) {
            cqlBuilder.append(columnHandler.getDataMap()).append(" = ? AND ");
        }
        cqlBuilder.setLength(cqlBuilder.length() - 4);
        cqlBuilder.append(';');
        String deleteCql = cqlBuilder.toString();
        cqlBuilder.setLength(0);
        this.deletePs = this.session.prepare(deleteCql);
        this.logger.debug("{} deleteCql:{}", this.table, deleteCql);
    }

    public final boolean exist(Object... keyValue) {
        ResultSetFuture rsf = this.session.executeAsync(this.inquireByKeyPs.bind(keyValue));
        ResultSet rs;
        Row r = null;
        try {
            rs = rsf.get();
            r = rs.one();
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }
        return r != null;
    }

    protected final PreparedStatement cachePrepare(String cql) {
        PreparedStatement ps = this.psCacheMap.get(cql);
        if (ps == null) {
            ps = this.session.prepare(cql);
            this.psCacheMap.put(cql, ps);
        }
        return ps;
    }

    protected final PreparedStatement prepare(String cql) {
        return this.session.prepare(cql);
    }

    protected final ResultSetFuture executeAsync(Statement stmnt) {
        return this.session.executeAsync(stmnt);
    }

    protected final T parseMap(Map<String, Object> entityMap) {
        T t = null;
        if (entityMap != null) {
            Field field;
            Object value;
            try {
                t = this.clazz.newInstance();
                for (ColumnHandler key : this.keyHandlerList) {
                    value = entityMap.get(key.getColumnName());
                    key.setFieldValue(t, value);
                }
                for (ColumnHandler column : this.columnHandlerList) {
                    value = entityMap.get(column.getColumnName());
                    column.setFieldValue(t, value);
                }
            } catch (InstantiationException | IllegalAccessException ex) {
            }
        }
        return t;
    }

    protected final Object getValue(Row row, ColumnHandler columnHander) {
        String name = columnHander.getDataMap();
        Object value = row.getObject(name);
        if(value == null) {
            value = columnHander.getDefaultValue();
        }
        return value;
    }

    protected final Map<String, Object> parseRow(Row r) {
        Map<String, Object> result = null;
        if (r != null) {
            result = new HashMap<>(this.columnHandlerList.size() + this.keyHandlerList.size(), 1);
            Object value;
            for (ColumnHandler ch : this.keyHandlerList) {
                value = this.getValue(r, ch);
                result.put(ch.getColumnName(), value);
            }
            for (ColumnHandler ch : this.columnHandlerList) {
                value = this.getValue(r, ch);
                result.put(ch.getColumnName(), value);
            }
        }
        return result;
    }

    public final T inquireByKey(Object... keyValue) {
        ResultSetFuture rsf = this.session.executeAsync(this.inquireByKeyPs.bind(keyValue));
        ResultSet rs;
        Row r = null;
        try {
            rs = rsf.get();
            r = rs.one();
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }
        Map<String, Object> result = this.parseRow(r);
        return this.parseMap(result);
    }

    public final void delete(Object... keyValue) {
        ResultSetFuture rsf = this.session.executeAsync(this.deletePs.bind(keyValue));
        try {
            rsf.get();
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }
    }
}
