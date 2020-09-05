package com.wolf.framework.dao.cassandra;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.exceptions.InvalidQueryException;
import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.dao.ColumnDataType;
import com.wolf.framework.dao.ColumnHandler;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.logger.LogFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author jianying9
 * @param <T>
 */
public abstract class AbstractCDao<T extends Entity> implements CDao<T> {

    protected final List<ColumnHandler> keyHandlerList;
    protected final List<ColumnHandler> columnHandlerList;
    protected final Class<T> clazz;
    protected final Logger logger = LogFactory.getLogger(FrameworkLogger.DAO);
    protected final String keyspace;
    protected final String table;
    private final Session session;
    private final String inquireBuyKeyCql;
    protected final String deleteCql;
    protected final String insertCql;
    private final boolean counter;

    private final Map<String, PreparedStatement> psCacheMap = new HashMap(2, 1);

    public AbstractCDao(
            boolean counter,
            Session session,
            String keyspace,
            String table,
            List<ColumnHandler> keyHandlerList,
            List<ColumnHandler> columnHandlerList,
            Class<T> clazz) {
        this.counter = counter;
        this.session = session;
        this.keyspace = keyspace;
        this.table = table;
        this.keyHandlerList = keyHandlerList;
        this.columnHandlerList = columnHandlerList;
        this.clazz = clazz;
        //
        StringBuilder cqlBuilder = new StringBuilder(128);
        //inquire by key
        cqlBuilder.append("SELECT ");
        for (ColumnHandler ch : this.keyHandlerList) {
            cqlBuilder.append(ch.getDataMap()).append(", ");
        }
        for (ColumnHandler ch : this.columnHandlerList) {
            cqlBuilder.append(ch.getDataMap()).append(", ");
        }
        cqlBuilder.setLength(cqlBuilder.length() - 2);
        cqlBuilder.append(" FROM ").append(this.keyspace)
                .append('.').append(this.table).append(" WHERE ");
        for (ColumnHandler columnHandler : this.keyHandlerList) {
            cqlBuilder.append(columnHandler.getDataMap()).append(" = ? AND ");
        }
        cqlBuilder.setLength(cqlBuilder.length() - 4);
        cqlBuilder.append(';');
        this.inquireBuyKeyCql = cqlBuilder.toString();
        this.logger.debug("{} inquireByKeyCql:{}", this.table, this.inquireBuyKeyCql);
        //delete
        cqlBuilder.setLength(0);
        cqlBuilder.append("DELETE FROM ").append(this.keyspace).append('.')
                .append(this.table).append(" WHERE ");
        for (ColumnHandler columnHandler : this.keyHandlerList) {
            cqlBuilder.append(columnHandler.getDataMap()).append(" = ? AND ");
        }
        cqlBuilder.setLength(cqlBuilder.length() - 4);
        cqlBuilder.append(';');
        this.deleteCql = cqlBuilder.toString();
        this.logger.debug("{} deleteCql:{}", this.table, this.deleteCql);
        // insert
        cqlBuilder.setLength(0);
        cqlBuilder.append("INSERT INTO ").append(this.keyspace).append('.')
                .append(this.table).append('(');
        for (ColumnHandler ch : this.keyHandlerList) {
            cqlBuilder.append(ch.getDataMap()).append(", ");
        }
        for (ColumnHandler ch : this.columnHandlerList) {
            cqlBuilder.append(ch.getDataMap()).append(", ");
        }
        cqlBuilder.setLength(cqlBuilder.length() - 2);
        cqlBuilder.append(") values (");
        long num = this.columnHandlerList.size() + this.keyHandlerList.size();
        while (num > 0) {
            cqlBuilder.append("?, ");
            num--;
        }
        cqlBuilder.setLength(cqlBuilder.length() - 2);
        cqlBuilder.append(");");
        this.insertCql = cqlBuilder.toString();
        this.logger.debug("{} insertCql:{}", this.table, this.insertCql);
    }

    public List<ColumnHandler> getKeyHandlerList() {
        return keyHandlerList;
    }

    public String getKeyspace() {
        return keyspace;
    }

    public String getTable() {
        return table;
    }

    private String getBasicDataType(ColumnDataType columnDataType) {
        String dataType;
        switch (columnDataType) {
            case LONG:
                dataType = "bigint";
                break;
            case INT:
                dataType = "int";
                break;
            case DOUBLE:
                dataType = "double";
                break;
            case STRING:
                dataType = "text";
                break;
            case BOOLEAN:
                dataType = "boolean";
                break;
            default:
                dataType = "unknown";
        }
        return dataType;
    }

    private void checkNewColumn(String result) {
        String columnName = result.replace("Undefined name ", "").replace(" in selection clause", "");
        if (this.counter) {
            for (ColumnHandler columnHandler : this.columnHandlerList) {
                if (columnHandler.getDataMap().equals(columnName)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("alter table ").append(this.keyspace).append(".").append(this.table)
                            .append(" add ").append(columnName).append(" ").append("counter").append(";");
                    System.out.println(sb.toString());
                    this.session.execute(sb.toString());
                    break;
                }
            }
        } else {
            for (ColumnHandler columnHandler : this.columnHandlerList) {
                if (columnHandler.getDataMap().equals(columnName)) {
                    StringBuilder sb = new StringBuilder();
                    String parameterDataType;
                    String dataType;
                    switch (columnHandler.getColumnDataType()) {
                        case LIST:
                            parameterDataType = this.getBasicDataType(columnHandler.getFirstParameterDataType());
                            dataType = "list<" + parameterDataType + ">";
                            break;
                        case SET:
                            parameterDataType = this.getBasicDataType(columnHandler.getFirstParameterDataType());
                            dataType = "set<" + parameterDataType + ">";
                            break;
                        case MAP:
                            parameterDataType = this.getBasicDataType(columnHandler.getFirstParameterDataType());
                            dataType = "map<" + parameterDataType + ",";
                            parameterDataType = this.getBasicDataType(columnHandler.getSecondParameterDataType());
                            dataType = dataType + parameterDataType + ">";
                            break;
                        default:
                            dataType = this.getBasicDataType(columnHandler.getColumnDataType());
                    }
                    sb.append("alter table ").append(this.keyspace).append(".").append(this.table)
                            .append(" add ").append(columnName).append(" ").append(dataType).append(";");
                    System.out.println(sb.toString());
                    this.session.execute(sb.toString());
                    break;
                }
            }
        }
        //重新检测
        this.check();
    }

    public String check() {
        String result = "";
        try {
            this.session.prepare(this.inquireBuyKeyCql);
        } catch (InvalidQueryException e) {
            result = e.getMessage();
            if (this.counter == false) {
                //普通表
                if (result.contains("unconfigured columnfamily")) {
                    //自动创建表
                    StringBuilder sb = new StringBuilder();
                    String dataType;
                    sb.append("create table ").append(this.keyspace).append(".").append(this.table).append(" (");
                    List<ColumnHandler> allList = new ArrayList();
                    allList.addAll(this.keyHandlerList);
                    allList.addAll(this.columnHandlerList);
                    String parameterDataType;
                    for (ColumnHandler columnHandler : allList) {
                        sb.append(columnHandler.getDataMap());
                        switch (columnHandler.getColumnDataType()) {
                            case LIST:
                                parameterDataType = this.getBasicDataType(columnHandler.getFirstParameterDataType());
                                dataType = "list<" + parameterDataType + ">";
                                break;
                            case SET:
                                parameterDataType = this.getBasicDataType(columnHandler.getFirstParameterDataType());
                                dataType = "set<" + parameterDataType + ">";
                                break;
                            case MAP:
                                parameterDataType = this.getBasicDataType(columnHandler.getFirstParameterDataType());
                                dataType = "map<" + parameterDataType + ",";
                                parameterDataType = this.getBasicDataType(columnHandler.getSecondParameterDataType());
                                dataType = dataType + parameterDataType + ">";
                                break;
                            default:
                                dataType = this.getBasicDataType(columnHandler.getColumnDataType());
                        }
                        sb.append(" ").append(dataType).append(",");
                    }
                    //处理主键
                    sb.append(" primary key(");
                    for (ColumnHandler columnHandler : this.keyHandlerList) {
                        sb.append(columnHandler.getDataMap()).append(",");
                    }
                    sb.setLength(sb.length() - 1);
                    sb.append("));");
                    System.out.println(sb.toString());
                    this.session.execute(sb.toString());
                } else if (result.contains("Undefined name")) {
                    this.checkNewColumn(result);
                } else {
                    result = "cassandra[" + this.keyspace + "." + this.table + "]:" + e.getMessage();
                    System.err.println(result);
                }
            } else //计数表
            {
                if (result.contains("unconfigured columnfamily")) {
                    //自动创建表
                    StringBuilder sb = new StringBuilder();
                    String dataType;
                    sb.append("create table ").append(this.keyspace).append(".").append(this.table).append(" (");
                    for (ColumnHandler columnHandler : keyHandlerList) {
                        sb.append(columnHandler.getDataMap());
                        dataType = this.getBasicDataType(columnHandler.getColumnDataType());
                        sb.append(" ").append(dataType).append(",");
                    }
                    for (ColumnHandler columnHandler : columnHandlerList) {
                        sb.append(columnHandler.getDataMap());
                        sb.append(" ").append("counter").append(",");
                    }
                    //处理主键
                    sb.append(" primary key(");
                    for (ColumnHandler columnHandler : this.keyHandlerList) {
                        sb.append(columnHandler.getDataMap()).append(",");
                    }
                    sb.setLength(sb.length() - 1);
                    sb.append("));");
                    System.out.println(sb.toString());
                    this.session.execute(sb.toString());
                } else if (result.contains("Undefined name")) {
                    this.checkNewColumn(result);
                } else {
                    result = "cassandra[" + this.keyspace + "." + this.table + "]:" + e.getMessage();
                    System.err.println(result);
                }
            }
        }
        return result;
    }

    @Override
    public final boolean exist(Object... keyValue) {
        PreparedStatement ps = this.cachePrepare(this.inquireBuyKeyCql);
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

    protected final PreparedStatement cachePrepare(String cql) {
        cql = cql.replace("${default}", this.keyspace);
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
        if (value == null) {
            value = columnHander.getDefaultValue();
        } else if (String.class.isInstance(value)) {
            String str = (String) value;
            if (str.isEmpty()) {
                value = columnHander.getDefaultValue();
            }
        }
        return value;
    }

    protected final Map<String, Object> parseRow(Row r) {
        Map<String, Object> result = null;
        if (r != null) {
            result = new HashMap(this.columnHandlerList.size() + this.keyHandlerList.size(), 1);
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

    @Override
    public final T inquireByKey(Object... keyValue) {
        PreparedStatement ps = this.cachePrepare(this.inquireBuyKeyCql);
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

    @Override
    public final void delete(Object... keyValue) {
        PreparedStatement ps = this.cachePrepare(this.deleteCql);
        ResultSetFuture rsf = this.session.executeAsync(ps.bind(keyValue));
        try {
            rsf.get();
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<T> query(String cql, Object... values) {
        List<T> resultList = Collections.EMPTY_LIST;
        PreparedStatement ps = this.cachePrepare(cql);
        ResultSetFuture rsf = this.executeAsync(ps.bind(values));
        ResultSet rs;
        try {
            rs = rsf.get();
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }
        List<Row> rList = rs.all();
        if (rList.isEmpty() == false) {
            resultList = new ArrayList(rList.size());
            Map<String, Object> map;
            T t;
            for (Row r : rList) {
                map = this.parseRow(r);
                t = this.parseMap(map);
                resultList.add(t);
            }
        }
        return resultList;
    }
}
