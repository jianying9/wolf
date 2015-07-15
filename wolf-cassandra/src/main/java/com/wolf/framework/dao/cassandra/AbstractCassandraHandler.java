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
import java.util.ArrayList;
import java.util.Collections;
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
public abstract class AbstractCassandraHandler implements CassandraHandler {

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
        this.logger.debug("{} deleteCql:{}", this.table, this.deleteCql);
        cqlBuilder.setLength(0);
        //count
        cqlBuilder.append("SELECT COUNT(*) FROM ").append(this.keyspace)
                .append('.').append(this.table).append(';');
        this.countCql = cqlBuilder.toString();
        cqlBuilder.setLength(0);
        this.logger.debug("{} countCql:{}", this.table, this.countCql);
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

    @Override
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

    private Map<String, Object> parseRow(Row r) {
        Map<String, Object> result = null;
        if (r != null) {
            result = new HashMap<String, Object>(this.columnHandlerList.size() + this.keyHandlerList.size(), 1);
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

    @Override
    public final Map<String, Object> queryByKey(Object... keyValues) {
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
        Map<String, Object> result = this.parseRow(r);
        return result;
    }

    @Override
    public final ResultSet execute(String cql, Object... values) {
        PreparedStatement ps = this.session.prepare(cql);
        ResultSetFuture rsf = this.session.executeAsync(ps.bind(values));
        ResultSet rs;
        try {
            rs = rsf.get();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        } catch (ExecutionException ex) {
            throw new RuntimeException(ex);
        }
        return rs;
    }

    @Override
    public List<Map<String, Object>> query(String cql, Object... values) {
        List<Map<String, Object>> resultList = Collections.EMPTY_LIST;
        PreparedStatement ps = this.session.prepare(cql);
        ResultSetFuture rsf = this.session.executeAsync(ps.bind(values));
        ResultSet rs;
        try {
            rs = rsf.get();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        } catch (ExecutionException ex) {
            throw new RuntimeException(ex);
        }
        List<Row> rList = rs.all();
        if (rList.isEmpty() == false) {
            resultList = new ArrayList<Map<String, Object>>(rList.size());
            Map<String, Object> map;
            for (Row r : rList) {
                map = this.parseRow(r);
                resultList.add(map);
            }
        }
        return resultList;
    }

    @Override
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

    @Override
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

    @Override
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
