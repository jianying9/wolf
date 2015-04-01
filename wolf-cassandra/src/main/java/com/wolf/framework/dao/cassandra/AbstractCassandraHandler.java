package com.wolf.framework.dao.cassandra;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import static com.datastax.driver.core.querybuilder.QueryBuilder.in;
import com.datastax.driver.core.querybuilder.Select;
import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.dao.ColumnHandler;
import com.wolf.framework.logger.LogFactory;
import java.util.ArrayList;
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
    protected final ColumnHandler keyColumnHandler;
    protected final List<ColumnHandler> columnHandlerList;
    private final String inquireByKeyCql;
    private final String deleteCql;
    private final String countCql;

    public AbstractCassandraHandler(
            Session session,
            String keyspace,
            String table,
            ColumnHandler keyColumnHandler,
            List<ColumnHandler> columnHandlerList
    ) {
        this.session = session;
        this.keyspace = keyspace;
        this.table = table;
        this.keyColumnHandler = keyColumnHandler;
        this.columnHandlerList = columnHandlerList;
        final String keyDataMap = this.keyColumnHandler.getDataMap();
        StringBuilder cqlBuilder = new StringBuilder(128);
        //inquire by key
        cqlBuilder.append("SELECT ").append(keyDataMap).append(", ");
        if (this.columnHandlerList.isEmpty() == false) {
            for (ColumnHandler columnHandler : this.columnHandlerList) {
                cqlBuilder.append(columnHandler.getDataMap()).append(", ");
            }
        }
        cqlBuilder.setLength(cqlBuilder.length() - 2);
        cqlBuilder.append(" FROM  ").append(this.keyspace)
                .append('.').append(this.table).append(" WHERE ")
                .append(keyDataMap).append(" = ?;");
        this.inquireByKeyCql = cqlBuilder.toString();
        cqlBuilder.setLength(0);
        this.logger.debug("{} inquireByKeyCql:{}", this.table, this.inquireByKeyCql);
        //delete
        cqlBuilder.append("DELETE FROM ").append(this.keyspace).append('.')
                .append(this.table).append(" WHERE ").append(keyDataMap)
                .append(" = ?;");
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

    public final boolean exist(String keyValue) {
        PreparedStatement ps = this.session.prepare(this.inquireByKeyCql);
        ResultSetFuture rsf = this.session.executeAsync(ps.bind(keyValue));
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

    public final Map<String, String> inquireByKey(String keyValue) {
        Map<String, String> result = null;
        PreparedStatement ps = this.session.prepare(this.inquireByKeyCql);
        ResultSetFuture rsf = this.session.executeAsync(ps.bind(keyValue));
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
            String value;
            ColumnHandler ch;
            result = new HashMap<String, String>(this.columnHandlerList.size() + 1);
            result.put(this.keyColumnHandler.getColumnName(), r.getString(0));
            for (int index = 0; index < this.columnHandlerList.size(); index++) {
                ch = this.columnHandlerList.get(index);
                value = r.getString(index + 1);
                if (value == null || value.equals("null")) {
                    value = ch.getDefaultValue();
                }
                result.put(ch.getColumnName(), value);
            }
        }
        return result;
    }

    public final List<Map<String, String>> inquireBykeys(List<String> keyValueList) {
        final String keyDataMap = this.keyColumnHandler.getDataMap();
        final String keyColumnName = this.keyColumnHandler.getColumnName();
        List<Map<String, String>> resultList = new ArrayList<Map<String, String>>(keyValueList.size());
        Select select = QueryBuilder.select().all().from(this.keyspace, this.table);
        select.where(in(keyDataMap, keyValueList));
        PreparedStatement ps = this.session.prepare(select.toString());
        ResultSetFuture rsf = this.session.executeAsync(ps.bind());
        ResultSet rs;
        Map<String, String> result;
        String value;
        ColumnHandler ch;
        try {
            rs = rsf.get();
            for (Row r : rs) {
                result = new HashMap<String, String>(this.columnHandlerList.size() + 1, 1);
                result.put(this.keyColumnHandler.getColumnName(), r.getString(0));
                result.put(keyColumnName, r.getString(0));
                for (int index = 0; index < this.columnHandlerList.size(); index++) {
                    ch = this.columnHandlerList.get(index);
                    value = r.getString(index + 1);
                    if (value == null || value.equals("null")) {
                        value = ch.getDefaultValue();
                    }
                    result.put(ch.getColumnName(), value);
                }
                resultList.add(result);
            }
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        } catch (ExecutionException ex) {
            throw new RuntimeException(ex);
        }
        return resultList;
    }

    public final void delete(String keyValue) {
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

    public final void batchDelete(List<String> keyValues) {
        if (keyValues.isEmpty() == false) {
            BatchStatement batch = new BatchStatement();
            PreparedStatement ps = this.session.prepare(this.deleteCql);
            for (String keyValue : keyValues) {
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
