package com.wolf.framework.dao.cassandra;

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
 * 普通表
 * @author jianying9
 */
public class CassandraHandlerImpl implements CassandraHandler {

    private final Logger logger = LogFactory.getLogger(FrameworkLogger.DAO);
    private final String keyspace;
    private final String table;
    private final Session session;
    private final String keyName;
    private final List<ColumnHandler> columnHandlerList;
    private final String inquireByKeyCql;
    private final String insertCql;
    private final String deleteCql;
    private final String countCql;

    public CassandraHandlerImpl(Session session, String keyspace, String table, String keyName, List<ColumnHandler> columnHandlerList) {
        this.session = session;
        this.keyspace = keyspace;
        this.table = table;
        this.keyName = keyName;
        this.columnHandlerList = columnHandlerList;
        StringBuilder cqlBuilder = new StringBuilder(256);
        //inquire by key
        cqlBuilder.append("SELECT * FROM ").append(this.keyspace)
                .append('.').append(this.table).append(" WHERE ")
                .append(this.keyName).append(" = ?;");
        this.inquireByKeyCql = cqlBuilder.toString();
        cqlBuilder.setLength(0);
        this.logger.debug("{} inquireByKeyCql:{}", this.table, this.inquireByKeyCql);
        // insert
        cqlBuilder.append("INSERT INTO ").append(this.keyspace).append('.')
                .append(this.table).append('(').append(this.keyName)
                .append(',');
        if (this.columnHandlerList.isEmpty() == false) {
            for (ColumnHandler ch : this.columnHandlerList) {
                cqlBuilder.append(ch.getColumnName()).append(',');
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
        this.logger.debug("{} insertCql:{}", this.table, this.inquireByKeyCql);
        //delete
        cqlBuilder.append("DELETE FROM ").append(this.keyspace).append('.')
                .append(this.table).append(" WHERE ").append(this.keyName)
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
        //
    }

    @Override
    public boolean exist(String keyValue) {
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

    @Override
    public Map<String, String> inquireByKey(String keyValue) {
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
            result = new HashMap<String, String>(this.columnHandlerList.size() + 1);
            result.put(this.keyName, r.getString(this.keyName));
            for (ColumnHandler ch : this.columnHandlerList) {
                value = r.getString(ch.getColumnName());
                if (value == null || value.equals("null")) {
                    value = ch.getDefaultValue();
                }
                result.put(ch.getColumnName(), value);
            }
        }
        return result;
    }

    @Override
    public List<Map<String, String>> inquireBykeys(List<String> keyValueList) {
        List<Map<String, String>> resultList = new ArrayList<Map<String, String>>(keyValueList.size());
        Select select = QueryBuilder.select().all().from(this.keyspace, this.table);
        select.where(in(this.keyName, keyValueList));
        PreparedStatement ps = this.session.prepare(select.toString());
        ResultSetFuture rsf = this.session.executeAsync(ps.bind());
        ResultSet rs;
        Map<String, String> result;
        String value;
        try {
            rs = rsf.get();
            for (Row r : rs) {
                result = new HashMap<String, String>(this.columnHandlerList.size() + 1);
                result.put(this.keyName, r.getString(this.keyName));
                for (ColumnHandler ch : this.columnHandlerList) {
                    value = r.getString(ch.getColumnName());
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

    @Override
    public String insert(Map<String, String> entityMap) {
        String keyValue = entityMap.get(this.keyName);
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
    public String update(Map<String, String> entityMap) {
        String keyValue = entityMap.get(this.keyName);
        if (keyValue == null) {
            throw new RuntimeException("Can not find keyValue when update:" + entityMap.toString());
        }
        StringBuilder cqlBuilder = new StringBuilder(256);
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
                cqlBuilder.append(ch.getColumnName()).append(" = ?, ");
            }
        }
        if (hasUpdate) {
            cqlBuilder.setLength(cqlBuilder.length() - 1);
            cqlBuilder.append(" WHERE ").append(this.keyName).append("= ? IF EXISTS;");
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
    public void delete(String keyValue) {
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
    public long count() {
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
