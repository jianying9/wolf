package com.wolf.framework.derby;

import com.wolf.framework.config.FrameworkLoggerEnum;
import com.wolf.framework.dao.annotation.ColumnTypeEnum;
import com.wolf.framework.dao.condition.Condition;
import com.wolf.framework.dao.condition.InquireContext;
import com.wolf.framework.dao.condition.Order;
import com.wolf.framework.dao.condition.OrderTypeEnum;
import com.wolf.framework.dao.parser.ColumnHandler;
import com.wolf.framework.data.DataHandler;
import com.wolf.framework.logger.LogFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.slf4j.Logger;

/**
 *
 * @author aladdin
 */
public class DerbyHandlerImpl extends AbstractDerbyHandler implements DerbyHandler {

    private final DataSource dataSource;
    private final String tableName;
    private final ColumnHandler keyHandler;
    private final List<ColumnHandler> columnHandlerList;
    private final String inquireByKeySql;
    private final String selectKeySqlModel;
    private final String selectSqlModel;
    private final String insertSql;
    private final String deleteSql;
    private final String countSqlModel;
    private final Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.DAO);

    public DerbyHandlerImpl(DataSource dataSource, String tableName, ColumnHandler keyHandler, List<ColumnHandler> columnHandlerList) {
        this.dataSource = dataSource;
        this.tableName = tableName;
        this.keyHandler = keyHandler;
        final String keyName = this.keyHandler.getColumnName();
        this.columnHandlerList = columnHandlerList;
        StringBuilder sqlBuilder = new StringBuilder(128);
        sqlBuilder.append(this.SELECT).append('*').append(this.FROM)
                .append('"').append(this.tableName).append('"').append(this.WHERE);
        this.selectSqlModel = sqlBuilder.toString();
        sqlBuilder.setLength(0);
        //
        sqlBuilder.append(this.selectSqlModel).append('"').append(keyName).append("\"=?");
        this.inquireByKeySql = sqlBuilder.toString();
        sqlBuilder.setLength(0);
        //
        sqlBuilder.append(this.SELECT).append('"').append(keyName).append('"')
                .append(this.FROM).append('"').append(this.tableName).append('"').append(this.WHERE);
        this.selectKeySqlModel = sqlBuilder.toString();
        sqlBuilder.setLength(0);
        //
        sqlBuilder.append(this.INSERT).append('"').append(this.tableName).append("\" (\"").append(keyName).append("\",");
        for (int index = 0; index < this.columnHandlerList.size(); index++) {
            sqlBuilder.append('"').append(this.columnHandlerList.get(index).getColumnName()).append("\",");
        }
        sqlBuilder.setLength(sqlBuilder.length() - 1);
        sqlBuilder.append(')').append(this.VALUES).append("(?,");
        for (int index = 0; index < this.columnHandlerList.size(); index++) {
            sqlBuilder.append("?,");
        }
        sqlBuilder.setLength(sqlBuilder.length() - 1);
        sqlBuilder.append(')');
        this.insertSql = sqlBuilder.toString();
        sqlBuilder.setLength(0);
        //
        sqlBuilder.append(this.DELETE).append('"').append(this.tableName).append('"')
                .append(this.WHERE).append('"').append(keyName).append("\"=?");
        this.deleteSql = sqlBuilder.toString();
        sqlBuilder.setLength(0);
        //
        sqlBuilder.append(this.SELECT).append(this.COUNT).append(this.FROM)
                .append('"').append(this.tableName).append('"').append(this.WHERE);
        this.countSqlModel = sqlBuilder.toString();
        sqlBuilder.setLength(0);
        //检测表是否存在
        this.logger.debug("chack, dose table:{} exist?", this.tableName);
        sqlBuilder.append(this.SELECT).append(this.COUNT).append(this.FROM).append("SYS.SYSTABLES").append(this.WHERE).append("tableName=?");
        final String checkSql = sqlBuilder.toString();
        this.logger.debug("check table sql:{}", checkSql);
        sqlBuilder.setLength(0);
        Connection conn = null;
        int count = 0;
        try {
            conn = dataSource.getConnection();
            PreparedStatement stat = conn.prepareStatement(checkSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            stat.setString(1, this.tableName);
            ResultSet rs = stat.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
            rs.close();
            stat.close();
        } catch (SQLException e) {
            Throwable throwable = null;
            for (Throwable t : e) {
                logger.error("count", t);
                throwable = t;
                break;
            }
            StringBuilder mesBuilder = new StringBuilder(200);
            mesBuilder.append("There was an error when check table:").append(this.tableName)
                    .append(".Cause:").append(throwable.getMessage());
            throw new RuntimeException(mesBuilder.toString());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
        if (count == 0) {
            this.logger.debug("table:{} does not exist!!!!", this.tableName);
            this.logger.debug("create table:{} ...", this.tableName);
            //创建表
            //够在建表sql
            DataHandler dataHandler;
            dataHandler = this.keyHandler.getDataHandler();
            String sqlType = this.getSqlType(dataHandler.getDataTypeEnum());
            sqlBuilder.append(this.CREATE_TABLE).append('"').append(this.tableName).append("\"(\"")
                    .append(keyName).append('"').append(sqlType).append(this.KEY).append(',');
            for (ColumnHandler columnHandler : columnHandlerList) {
                dataHandler = columnHandler.getDataHandler();
                sqlType = this.getSqlType(dataHandler.getDataTypeEnum());
                sqlBuilder.append('"').append(columnHandler.getColumnName()).append('"').append(sqlType).append(',');
            }
            sqlBuilder.setLength(sqlBuilder.length() - 1);
            sqlBuilder.append(')');
            String createSql = sqlBuilder.toString();
            sqlBuilder.setLength(0);
            this.logger.debug("create table sql:{}", createSql);
            //建表
            try {
                conn = dataSource.getConnection();
                PreparedStatement stat = conn.prepareStatement(createSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                stat.execute();
                stat.close();
            } catch (SQLException e) {
                Throwable throwable = null;
                for (Throwable t : e) {
                    logger.error("create table sql", t);
                    throwable = t;
                    break;
                }
                StringBuilder mesBuilder = new StringBuilder(200);
                mesBuilder.append("There was an error when create table:").append(this.tableName)
                        .append(".Cause:").append(throwable.getMessage());
                throw new RuntimeException(mesBuilder.toString());
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                    }
                }
            }
            //创建索引
            String indexSql;
            List<String> indexSqlList = new ArrayList<String>(columnHandlerList.size());
            for (ColumnHandler columnHandler : columnHandlerList) {
                if (columnHandler.getColumnType() == ColumnTypeEnum.INDEX) {
                    sqlBuilder.append(this.CREATE_INDEX).append("\"index_")
                            .append(this.tableName).append('_').append(columnHandler.getColumnName()).append('"')
                            .append(this.ON).append('"').append(this.tableName).append("\"(\"").append(columnHandler.getColumnName()).append("\")");
                    indexSql = sqlBuilder.toString();
                    indexSqlList.add(indexSql);
                    sqlBuilder.setLength(0);

                }
            }

            try {
                conn = dataSource.getConnection();
                Statement stat = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                for (String iSql : indexSqlList) {
                    this.logger.debug("create index sql:{}", iSql);
                    stat.execute(iSql);
                }
                stat.close();
            } catch (SQLException e) {
                Throwable throwable = null;
                for (Throwable t : e) {
                    logger.error("create index sql", t);
                    throwable = t;
                    break;
                }
                StringBuilder mesBuilder = new StringBuilder(200);
                mesBuilder.append("There was an error when create index:").append(this.tableName)
                        .append(".Cause:").append(throwable.getMessage());
                throw new RuntimeException(mesBuilder.toString());
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                    }
                }
            }
        } else {
            this.logger.debug("table:{} exist!!!!", this.tableName);
        }
    }

    @Override
    public Map<String, String> inquireByKey(String keyValue) {
        logger.debug("inquireByKey sql:{}", this.inquireByKeySql);
        logger.debug("value:{}", keyValue);
        Connection conn = null;
        Map<String, String> resultMap = null;
        try {
            conn = dataSource.getConnection();
            PreparedStatement stat = conn.prepareStatement(inquireByKeySql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            stat.setString(1, keyValue);
            ResultSet rs = stat.executeQuery();
            if (rs.next()) {
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                resultMap = new HashMap<String, String>(columnCount, 1);
                for (int index = 1; index <= columnCount; index++) {
                    resultMap.put(rsmd.getColumnLabel(index), rs.getString(index));
                }
            }
            rs.close();
            stat.close();
        } catch (SQLException e) {
            Throwable throwable = null;
            for (Throwable t : e) {
                logger.error("inquireByKey", t);
                throwable = t;
                break;
            }
            StringBuilder mesBuilder = new StringBuilder(200);
            mesBuilder.append("There was an error table:").append(this.tableName)
                    .append(" executing inquireByKey.Cause:").append(throwable.getMessage());
            throw new RuntimeException(mesBuilder.toString());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
        return resultMap;
    }

    @Override
    public List<Map<String, String>> inquireBykeys(List<String> keyValueList) {
        StringBuilder sqlBuilder = new StringBuilder(128);
        sqlBuilder.append(this.selectSqlModel).append('"')
                .append(this.keyHandler.getColumnName()).append('"').append(this.IN).append('(');
        for (String keyValue : keyValueList) {
            sqlBuilder.append(keyValue).append(',');
        }
        sqlBuilder.setLength(sqlBuilder.length() - 1);
        sqlBuilder.append(')');
        String sql = sqlBuilder.toString();
        if (logger.isDebugEnabled()) {
            logger.debug("inquireBykeys sql:{}", sql);
            logger.debug("values:{}", keyValueList.toString());
        }
        Connection conn = null;
        List<Map<String, String>> resultMapList;
        try {
            conn = this.dataSource.getConnection();
            PreparedStatement stat = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stat.executeQuery();
            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
            }
            if (rowCount > 0) {
                Map<String, String> resultMap;
                resultMapList = new ArrayList<Map<String, String>>(rowCount);
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                rs.beforeFirst();
                while (rs.next()) {
                    resultMap = new HashMap<String, String>(columnCount, 1);
                    for (int index = 1; index <= columnCount; index++) {
                        resultMap.put(rsmd.getColumnLabel(index), rs.getString(index));
                    }
                    resultMapList.add(resultMap);
                }
            } else {
                resultMapList = new ArrayList<Map<String, String>>(0);
            }
            rs.close();
            stat.close();
        } catch (SQLException e) {
            Throwable throwable = null;
            for (Throwable t : e) {
                logger.error("inquireBykeys", t);
                throwable = t;
                break;
            }
            StringBuilder mesBuilder = new StringBuilder(200);
            mesBuilder.append("There was an error when table:").append(this.tableName)
                    .append(" executing inquireBykeys.Cause:").append(throwable.getMessage());
            throw new RuntimeException(mesBuilder.toString());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
        return resultMapList;
    }

    private void createConditionSql(StringBuilder sqlBuilder, List<Condition> conditionList) {
        Condition condition;
        //条件
        for (int index = 0; index < conditionList.size(); index++) {
            condition = conditionList.get(index);
            sqlBuilder.append('"').append(condition.getColumnName()).append('"');
            switch (condition.getOperateTypeEnum()) {
                case EQUAL:
                    sqlBuilder.append(this.EQUAL);
                    break;
                case LIKE:
                    sqlBuilder.append(this.LIKE);
                    break;
                case NOT_EQUAL:
                    sqlBuilder.append(this.NOT_EQUAL);
                    break;
                case GREATER:
                    sqlBuilder.append(this.GREATER);
                    break;
                case GREATER_OR_EQUAL:
                    sqlBuilder.append(this.GREATER_OR_EQUAL);
                    break;
                case LESS:
                    sqlBuilder.append(this.LESS);
                    break;
                case LESS_OR_EQUAL:
                    sqlBuilder.append(this.LESS_OR_EQUAL);
                    break;
                case NOT_LIKE:
                    sqlBuilder.append(this.NOT_LIKE);
                    break;
            }
            sqlBuilder.append('?').append(this.AND);
        }
        sqlBuilder.setLength(sqlBuilder.length() - this.AND.length());
    }

    private void createInquireSql(StringBuilder sqlBuilder, InquireContext inquireContext) {
        List<Condition> conditionList = inquireContext.getConditionList();
        //条件
        this.createConditionSql(sqlBuilder, conditionList);
        //排序
        List<Order> orderList = inquireContext.getOrderList();
        if (orderList.isEmpty() == false) {
            sqlBuilder.append(this.ORDER_BY);
            for (Order order : orderList) {
                sqlBuilder.append('"').append(order.getColumnName()).append('"');
                if (order.getOrderType() == OrderTypeEnum.DESC) {
                    sqlBuilder.append(this.DESC);
                }
                sqlBuilder.append(',');
            }
            sqlBuilder.setLength(sqlBuilder.length() - 1);
        }
        //分页
        int pageIndex = inquireContext.getPageIndex();
        if (pageIndex > 0) {
            if (pageIndex > 1) {
                sqlBuilder.append(this.OFFSET).append(this.FETCH_NEXT);
            } else {
                sqlBuilder.append(this.FETCH_FIRST);
            }
        }
    }

    private List<String> getInquireValueList(InquireContext inquireContext) {
        List<Condition> conditionList = inquireContext.getConditionList();
        List<String> valueList = new ArrayList<String>(conditionList.size() + 2);
        //获取条件值
        for (int index = 0; index < conditionList.size(); index++) {
            valueList.add(conditionList.get(index).getColumnValue());
        }
        //获取分页条件值
        int pageIndex = inquireContext.getPageIndex();
        if (pageIndex > 0) {
            int pageSize = inquireContext.getPageSize();
            if (pageIndex > 1) {
                int offset = (pageIndex - 1) * pageSize;
                valueList.add(Long.toString(offset));
            }
            valueList.add(Long.toString(pageSize));
        }
        return valueList;
    }

    @Override
    public List<String> inquireKeys(InquireContext inquireContext) {
        StringBuilder sqlBuilder = new StringBuilder(128);
        sqlBuilder.append(this.selectKeySqlModel);
        this.createInquireSql(sqlBuilder, inquireContext);
        String sql = sqlBuilder.toString();
        List<String> valueList = this.getInquireValueList(inquireContext);
        if (logger.isDebugEnabled()) {
            logger.debug("inquireKeys sql:{}", sql);
            logger.debug("values:{}", valueList.toString());
        }
        Connection conn = null;
        List<String> keyValueList;
        try {
            conn = dataSource.getConnection();
            PreparedStatement stat = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            int columnIndex;
            for (int index = 0; index < valueList.size(); index++) {
                columnIndex = index + 1;
                stat.setString(columnIndex, valueList.get(index));
            }
            ResultSet rs = stat.executeQuery();
            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
            }
            if (rowCount > 0) {
                String keyValue;
                keyValueList = new ArrayList<String>(rowCount);
                rs.beforeFirst();
                while (rs.next()) {
                    keyValue = rs.getString(1);
                    keyValueList.add(keyValue);
                }
            } else {
                keyValueList = new ArrayList<String>(0);
            }
            rs.close();
            stat.close();
        } catch (SQLException e) {
            Throwable throwable = null;
            for (Throwable t : e) {
                logger.error("inquireKeys", t);
                throwable = t;
                break;
            }
            StringBuilder mesBuilder = new StringBuilder(200);
            mesBuilder.append("There was an error when table:").append(this.tableName)
                    .append(" executing inquireKeys.Cause:").append(throwable.getMessage());
            throw new RuntimeException(mesBuilder.toString());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
        return keyValueList;
    }

    @Override
    public List<Map<String, String>> inquire(InquireContext inquireContext) {
        StringBuilder sqlBuilder = new StringBuilder(128);
        sqlBuilder.append(this.selectSqlModel);
        this.createInquireSql(sqlBuilder, inquireContext);
        String sql = sqlBuilder.toString();
        List<String> valueList = this.getInquireValueList(inquireContext);
        if (logger.isDebugEnabled()) {
            logger.debug("inquire sql:{}", sql);
            logger.debug("values:{}", valueList.toString());
        }
        Connection conn = null;
        List<Map<String, String>> resultMapList = new ArrayList<Map<String, String>>(0);
        try {
            conn = dataSource.getConnection();
            PreparedStatement stat = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            int columnIndex;
            for (int index = 0; index < valueList.size(); index++) {
                columnIndex = index + 1;
                stat.setString(columnIndex, valueList.get(index));
            }
            ResultSet rs = stat.executeQuery();
            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
            }
            if (rowCount > 0) {
                Map<String, String> resultMap;
                resultMapList = new ArrayList<Map<String, String>>(rowCount);
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                rs.beforeFirst();
                while (rs.next()) {
                    resultMap = new HashMap<String, String>(columnCount, 1);
                    for (int index = 1; index <= columnCount; index++) {
                        resultMap.put(rsmd.getColumnLabel(index), rs.getString(index));
                    }
                    resultMapList.add(resultMap);
                }
            }
            rs.close();
            stat.close();
        } catch (SQLException e) {
            Throwable throwable = null;
            for (Throwable t : e) {
                logger.error("inquire", t);
                throwable = t;
                break;
            }
            StringBuilder mesBuilder = new StringBuilder(200);
            mesBuilder.append("There was an error when table:").append(this.tableName)
                    .append(" executing inquire.Cause:").append(throwable.getMessage());
            throw new RuntimeException(mesBuilder.toString());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
        return resultMapList;
    }

    @Override
    public int count(List<Condition> conditionList) {
        StringBuilder sqlBuilder = new StringBuilder(128);
        sqlBuilder.append(this.countSqlModel);
        this.createConditionSql(sqlBuilder, conditionList);
        String sql = sqlBuilder.toString();
        List<String> valueList = new ArrayList<String>(conditionList.size());
        //获取条件值
        for (int index = 0; index < conditionList.size(); index++) {
            valueList.add(conditionList.get(index).getColumnValue());
        }
        if (logger.isDebugEnabled()) {
            logger.debug("count sql:{}", sql);
            logger.debug("values:{}", valueList.toString());
        }
        Connection conn = null;
        int count = 0;
        try {
            conn = dataSource.getConnection();
            PreparedStatement stat = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            int columnIndex;
            for (int index = 0; index < valueList.size(); index++) {
                columnIndex = index + 1;
                stat.setString(columnIndex, valueList.get(index));
            }
            ResultSet rs = stat.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
            rs.close();
            stat.close();
        } catch (SQLException e) {
            Throwable throwable = null;
            for (Throwable t : e) {
                logger.error("count", t);
                throwable = t;
                break;
            }
            StringBuilder mesBuilder = new StringBuilder(200);
            mesBuilder.append("There was an error when table:").append(this.tableName)
                    .append(" executing count.Cause:").append(throwable.getMessage());
            throw new RuntimeException(mesBuilder.toString());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
        return count;
    }

    private List<String> getInsertValueList(Map<String, String> entityMap) {
        List<String> valueList = new ArrayList<String>(this.columnHandlerList.size() + 1);
        final String keyName = this.keyHandler.getColumnName();
        String keyValue = entityMap.get(keyName);
        if (keyValue == null) {
            throw new RuntimeException(this.tableName + " insert error: Can not find key:" + keyName + " value...");
        }
        valueList.add(keyValue);
        String columnName;
        String columnValue;
        for (int index = 0; index < this.columnHandlerList.size(); index++) {
            columnName = this.columnHandlerList.get(index).getColumnName();
            columnValue = entityMap.get(columnName);
            if (columnValue == null) {
                throw new RuntimeException(this.tableName + " insert error: Can not find column:" + columnName + " value...");
            }
            valueList.add(columnValue);
        }
        return valueList;
    }

    private List<List<String>> getInsertValuesList(List<Map<String, String>> entityMapList) {
        List<List<String>> valuesList = new ArrayList<List<String>>(entityMapList.size());
        List<String> valueList;
        for (Map<String, String> entityMap : entityMapList) {
            valueList = this.getInsertValueList(entityMap);
            valuesList.add(valueList);
        }
        return valuesList;
    }

    @Override
    public void insert(Map<String, String> entityMap) {
        List<String> valueList = this.getInsertValueList(entityMap);
        if (logger.isDebugEnabled()) {
            logger.debug("insert sql:{}", this.insertSql);
            logger.debug("values:{}", valueList.toString());
        }
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            PreparedStatement stat = conn.prepareStatement(insertSql);
            int columnIndex;
            for (int index = 0; index < valueList.size(); index++) {
                columnIndex = index + 1;
                stat.setString(columnIndex, valueList.get(index));
            }
            stat.executeUpdate();
            stat.close();
        } catch (SQLException e) {
            Throwable throwable = null;
            for (Throwable t : e) {
                logger.error("insert", t);
                throwable = t;
                break;
            }
            StringBuilder mesBuilder = new StringBuilder(200);
            mesBuilder.append("There was an error when table:").append(this.tableName)
                    .append(" executing insert.Cause: ").append(throwable.getMessage());
            throw new RuntimeException(mesBuilder.toString());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    @Override
    public Map<String, String> insertAndInquire(Map<String, String> entityMap) {
        Map<String, String> resultMap = null;
        List<String> valueList = this.getInsertValueList(entityMap);
        if (logger.isDebugEnabled()) {
            logger.debug("insert sql:{}", this.insertSql);
            logger.debug("values:{}", valueList.toString());
        }
        Connection conn = null;
        try {
            //插入
            conn = dataSource.getConnection();
            PreparedStatement stat = conn.prepareStatement(insertSql);
            int columnIndex;
            for (int index = 0; index < valueList.size(); index++) {
                columnIndex = index + 1;
                stat.setString(columnIndex, valueList.get(index));
            }
            stat.executeUpdate();
            stat.close();
            //获取key value
            String keyValue = entityMap.get(this.keyHandler.getColumnName());
            //查询
            logger.debug("inquireByKey sql:{}", this.inquireByKeySql);
            logger.debug("value:{}", keyValue);
            stat = conn.prepareStatement(inquireByKeySql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            stat.setString(1, keyValue);
            ResultSet rs = stat.executeQuery();
            if (rs.next()) {
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                resultMap = new HashMap<String, String>(columnCount, 1);
                for (int index = 1; index <= columnCount; index++) {
                    resultMap.put(rsmd.getColumnLabel(index), rs.getString(index));
                }
            }
            rs.close();
            stat.close();
        } catch (SQLException e) {
            Throwable throwable = null;
            for (Throwable t : e) {
                logger.error("insert and inquire", t);
                throwable = t;
                break;
            }
            StringBuilder mesBuilder = new StringBuilder(200);
            mesBuilder.append("There was an error when table:").append(this.tableName)
                    .append(" executing insert and inquire.Cause: ").append(throwable.getMessage());
            throw new RuntimeException(mesBuilder.toString());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
        return resultMap;
    }

    @Override
    public void batchInsert(List<Map<String, String>> entityMapList) {
        if (logger.isDebugEnabled()) {
            logger.debug("batch insert sql:{}", this.insertSql);
            logger.debug("values:{}", entityMapList.toString());
        }
        List<List<String>> valuesList = this.getInsertValuesList(entityMapList);
        Connection conn = null;
        try {
            conn = this.dataSource.getConnection();
            PreparedStatement stat = conn.prepareStatement(this.insertSql);
            int columnIndex;
            List<String> valueList;
            for (int rowIndex = 0; rowIndex < valuesList.size(); rowIndex++) {
                valueList = valuesList.get(rowIndex);
                for (int index = 0; index < valueList.size(); index++) {
                    columnIndex = index + 1;
                    stat.setString(columnIndex, valueList.get(index));
                }
                stat.addBatch();
            }
            stat.executeBatch();
            stat.close();
        } catch (SQLException e) {
            Throwable throwable = null;
            for (Throwable t : e) {
                logger.error("batch insert", t);
                throwable = t;
                break;
            }
            StringBuilder mesBuilder = new StringBuilder(200);
            mesBuilder.append("There was an error when table:").append(this.tableName)
                    .append(" executing batch insert.Cause: ").append(throwable.getMessage());
            throw new RuntimeException(mesBuilder.toString());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    @Override
    public void delete(String keyValue) {
        this.logger.debug("delete sql:{}", this.deleteSql);
        this.logger.debug("value:{}", keyValue);
        Connection conn = null;
        try {
            conn = this.dataSource.getConnection();
            PreparedStatement stat = conn.prepareStatement(this.deleteSql);
            stat.setString(1, keyValue);
            stat.executeUpdate();
            stat.close();
        } catch (SQLException e) {
            Throwable throwable = null;
            for (Throwable t : e) {
                this.logger.error("delete", t);
                throwable = t;
                break;
            }
            StringBuilder mesBuilder = new StringBuilder(200);
            mesBuilder.append("There was an error when table:").append(this.tableName)
                    .append(" executing delete.Cause: ").append(throwable.getMessage());
            throw new RuntimeException(mesBuilder.toString());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    @Override
    public void batchDelete(List<String> keyValueList) {
        if (logger.isDebugEnabled()) {
            logger.debug("batch delete sql:{}", this.deleteSql);
            logger.debug("value:{}", keyValueList.toString());
        }
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            PreparedStatement stat = conn.prepareStatement(this.deleteSql);
            for (String keyValue : keyValueList) {
                stat.setString(1, keyValue);
                stat.addBatch();
            }
            stat.executeBatch();
            stat.close();
        } catch (SQLException e) {
            Throwable throwable = null;
            for (Throwable t : e) {
                logger.error("batch delete", t);
                throwable = t;
                break;
            }
            StringBuilder mesBuilder = new StringBuilder(200);
            mesBuilder.append("There was an error when table:").append(this.tableName)
                    .append(" executing batch delete.Cause: ").append(throwable.getMessage());
            throw new RuntimeException(mesBuilder.toString());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    private void createUpdateSql(StringBuilder sqlBuilder, Map<String, String> entityMap) {
        final String keyName = this.keyHandler.getColumnName();
        String keyValue = entityMap.get(keyName);
        if (keyValue == null) {
            throw new RuntimeException(this.tableName + " update error: Can not find key:" + keyName + " value...");
        }
        String columnName;
        String columnValue;
        boolean canUpdate = false;
        for (int index = 0; index < this.columnHandlerList.size(); index++) {
            columnName = this.columnHandlerList.get(index).getColumnName();
            columnValue = entityMap.get(columnName);
            if (columnValue != null) {
                canUpdate = true;
                sqlBuilder.append('"').append(columnName).append("\"=?,");
            }
        }
        if (canUpdate == false) {
            throw new RuntimeException(this.tableName + " update error: Can not find any update value...");
        }
        sqlBuilder.setLength(sqlBuilder.length() - 1);
        sqlBuilder.append(this.WHERE).append('"').append(keyName).append("\"=?");
    }

    private List<String> getUpdateValueList(Map<String, String> entityMap) {
        String columnName;
        String columnValue;
        List<String> valueList = new ArrayList<String>(entityMap.size());
        for (int index = 0; index < this.columnHandlerList.size(); index++) {
            columnName = this.columnHandlerList.get(index).getColumnName();
            columnValue = entityMap.get(columnName);
            if (columnValue != null) {
                valueList.add(columnValue);
            }
        }
        String keyValue = entityMap.get(this.keyHandler.getColumnName());
        valueList.add(keyValue);
        return valueList;
    }

    private List<List<String>> getUpdateValuesList(List<Map<String, String>> entityMapList) {
        List<List<String>> valuesList = new ArrayList<List<String>>(entityMapList.size());
        List<String> valueList;
        for (Map<String, String> entityMap : entityMapList) {
            valueList = this.getUpdateValueList(entityMap);
            valuesList.add(valueList);
        }
        return valuesList;
    }

    @Override
    public void update(Map<String, String> entityMap) {
        StringBuilder sqlBuilder = new StringBuilder(128);
        sqlBuilder.append(this.UPDATE).append('"').append(this.tableName).append('"').append(this.SET);
        this.createUpdateSql(sqlBuilder, entityMap);
        String sql = sqlBuilder.toString();
        List<String> valueList = this.getUpdateValueList(entityMap);
        if (logger.isDebugEnabled()) {
            logger.debug("update sql:{}", sql);
            logger.debug("values:{}", valueList.toString());
        }
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            PreparedStatement stat = conn.prepareStatement(sql);
            int columnIndex;
            for (int index = 0; index < valueList.size(); index++) {
                columnIndex = index + 1;
                stat.setString(columnIndex, valueList.get(index));
            }
            stat.executeUpdate();
            stat.close();
        } catch (SQLException e) {
            Throwable throwable = null;
            for (Throwable t : e) {
                logger.error("update", t);
                throwable = t;
                break;
            }
            StringBuilder mesBuilder = new StringBuilder(200);
            mesBuilder.append("There was an error when table:").append(this.tableName)
                    .append(" executing update.Cause: ").append(throwable.getMessage());
            throw new RuntimeException(mesBuilder.toString());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    @Override
    public void batchUpdate(List<Map<String, String>> entityMapList) {
        StringBuilder sqlBuilder = new StringBuilder(128);
        sqlBuilder.append(this.UPDATE).append('"').append(this.tableName).append('"').append(this.SET);
        this.createUpdateSql(sqlBuilder, entityMapList.get(0));
        String sql = sqlBuilder.toString();
        if (logger.isDebugEnabled()) {
            logger.debug("batch update sql:{}", sql);
            logger.debug("values:{}", entityMapList.toString());
        }
        List<List<String>> valuesList = this.getUpdateValuesList(entityMapList);
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            PreparedStatement stat = conn.prepareStatement(sql);
            int columnIndex;
            List<String> valueList;
            for (int rowIndex = 0; rowIndex < valuesList.size(); rowIndex++) {
                valueList = valuesList.get(rowIndex);
                for (int index = 0; index < valueList.size(); index++) {
                    columnIndex = index + 1;
                    stat.setString(columnIndex, valueList.get(index));
                }
                stat.addBatch();
            }
            stat.executeBatch();
            stat.close();
        } catch (SQLException e) {
            Throwable throwable = null;
            for (Throwable t : e) {
                logger.error("batch update", t);
                throwable = t;
                break;
            }
            StringBuilder mesBuilder = new StringBuilder(200);
            mesBuilder.append("There was an error when table:").append(this.tableName)
                    .append(" executing batch update.Cause: ").append(throwable.getMessage());
            throw new RuntimeException(mesBuilder.toString());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    @Override
    public Map<String, String> updateAndInquire(Map<String, String> entityMap) {
        Map<String, String> resultMap = null;
        StringBuilder sqlBuilder = new StringBuilder(128);
        sqlBuilder.append(this.UPDATE).append('"').append(this.tableName).append('"').append(this.SET);
        this.createUpdateSql(sqlBuilder, entityMap);
        String sql = sqlBuilder.toString();
        List<String> valueList = this.getUpdateValueList(entityMap);
        if (logger.isDebugEnabled()) {
            logger.debug("update sql:{}", sql);
            logger.debug("values:{}", valueList.toString());
        }
        Connection conn = null;
        try {
            //插入
            conn = dataSource.getConnection();
            PreparedStatement stat = conn.prepareStatement(insertSql);
            int columnIndex;
            for (int index = 0; index < valueList.size(); index++) {
                columnIndex = index + 1;
                stat.setString(columnIndex, valueList.get(index));
            }
            stat.executeUpdate();
            stat.close();
            //获取key value
            String keyValue = entityMap.get(this.keyHandler.getColumnName());
            //查询
            logger.debug("inquireByKey sql:{}", this.inquireByKeySql);
            logger.debug("value:{}", keyValue);
            stat = conn.prepareStatement(inquireByKeySql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            stat.setString(1, keyValue);
            ResultSet rs = stat.executeQuery();
            if (rs.next()) {
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                resultMap = new HashMap<String, String>(columnCount, 1);
                for (int index = 1; index <= columnCount; index++) {
                    resultMap.put(rsmd.getColumnLabel(index), rs.getString(index));
                }
            }
            rs.close();
            stat.close();
        } catch (SQLException e) {
            Throwable throwable = null;
            for (Throwable t : e) {
                logger.error("update and inquire", t);
                throwable = t;
                break;
            }
            StringBuilder mesBuilder = new StringBuilder(200);
            mesBuilder.append("There was an error when table:").append(this.tableName)
                    .append(" executing update and inquire.Cause: ").append(throwable.getMessage());
            throw new RuntimeException(mesBuilder.toString());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
        return resultMap;
    }
}
