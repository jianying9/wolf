package com.wolf.framework.dao.cassandra;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
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
    protected final Session session;
    private final String inquireByKeyCql;
    protected final String deleteCql;

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
        cqlBuilder.setLength(0);
        this.logger.debug("{} deleteCql:{}", this.table, this.deleteCql);
    }

    public final boolean exist(Object... keyValue) {
        PreparedStatement ps = this.session.prepare(this.inquireByKeyCql);
        ResultSetFuture rsf = this.session.executeAsync(ps.bind(keyValue));
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

    protected final T parseMap(Map<String, Object> entityMap) {
        T t = null;
        if (entityMap != null) {
            Field field;
            Object value;
            try {
                t = this.clazz.newInstance();
                for (ColumnHandler key : this.keyHandlerList) {
                    value = entityMap.get(key.getColumnName());
                    field = key.getField();
                    field.setAccessible(true);
                    field.set(t, value);
                    field.setAccessible(false);
                }
                for (ColumnHandler key : this.columnHandlerList) {
                    value = entityMap.get(key.getColumnName());
                    field = key.getField();
                    field.setAccessible(true);
                    field.set(t, value);
                    field.setAccessible(false);
                }
            } catch (InstantiationException | IllegalAccessException ex) {
            }
        }
        return t;
    }
    
    protected final Object getValue(Row row, ColumnHandler columnHander) {
        Object result;
        String name = columnHander.getDataMap();
        switch (columnHander.getColumnDataType()) {
            case STRING:
                result = row.getString(name);
                break;
            case LONG:
                result = row.getLong(name);
                break;
            case INT:
                result = row.getInt(name);
                break;
            case DOUBLE:
                result = row.getDouble(name);
                break;
            default:
                result = row.getBool(name);
        }
        return result;
    }
    
    protected final Map<String, Object> parseRow(Row r) {
        Map<String, Object> result = null;
        if (r != null) {
            result = new HashMap<>(this.columnHandlerList.size() + this.keyHandlerList.size(), 1);
            Object value;
            for (ColumnHandler ch : this.keyHandlerList) {
                value = this.getValue(r, ch);
                if (value == null || value.equals("null")) {
                    value = ch.getDefaultValue();
                }
                result.put(ch.getColumnName(), value);
            }
            for (ColumnHandler ch : this.columnHandlerList) {
                value = this.getValue(r, ch);
                if (value == null || value.equals("null")) {
                    value = ch.getDefaultValue();
                }
                result.put(ch.getColumnName(), value);
            }
        }
        return result;
    }

    public final T inquireByKey(Object... keyValue) {
        PreparedStatement ps = this.session.prepare(this.inquireByKeyCql);
        ResultSetFuture rsf = this.session.executeAsync(ps.bind(keyValue));
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
        PreparedStatement ps = this.session.prepare(this.deleteCql);
        ResultSetFuture rsf = this.session.executeAsync(ps.bind(keyValue));
        try {
            rsf.get();
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }
    }
}
