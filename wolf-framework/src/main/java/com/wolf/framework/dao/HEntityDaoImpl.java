package com.wolf.framework.dao;

import com.wolf.framework.dao.parser.HColumnHandler;
import com.wolf.framework.hbase.HTableHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

/**
 *
 * @author aladdin
 */
public class HEntityDaoImpl<T extends Entity> extends AbstractDaoHandler<T> implements HEntityDao<T> {

    private final String tableName;
    private final HTableHandler hTableHandler;
    private final HColumnHandler keyHandler;
    private final List<HColumnHandler> columnHandlerList;

    public HEntityDaoImpl(String tableName, HTableHandler hTableHandler, Class<T> clazz, HColumnHandler keyHandler, List<HColumnHandler> columnHandlerList) {
        super(clazz);
        this.tableName = tableName;
        this.hTableHandler = hTableHandler;
        this.keyHandler = keyHandler;
        this.columnHandlerList = columnHandlerList;
    }
    
    private Map<String, String> readResultToMap(Result result) {
        Map<String, String> resultMap = new HashMap<String, String>(this.columnHandlerList.size() + 1, 1);
        //放入key
        String keyValue = Bytes.toString(result.getRow());
        resultMap.put(this.keyHandler.getColumnName(), keyValue);
        //读取column
        String columnName;
        byte[] columnValue;
        for (HColumnHandler columnHandler : columnHandlerList) {
            columnName = columnHandler.getColumnName();
            columnValue = result.getValue(HTableHandler.COLUMN_FAMILY, Bytes.toBytes(columnName));
            if (columnValue == null) {
                this.logger.warn("inquire table {} waring message: can not find column:{} value", this.tableName, columnName);
                resultMap.put(columnName, "");
            } else {
                resultMap.put(columnName, Bytes.toString(columnValue));
            }
        }
        return resultMap;
    }

    private T readResult(Result result) {
        Map<String, String> resultMap = this.readResultToMap(result);
        return this.newInstance(resultMap);
    }

    private List<T> readResult(Result[] result) {
        List<T> tList = new ArrayList<T>(result.length);
        T t;
        for (int index = 0; index < result.length; index++) {
            t = this.readResult(result[index]);
            tList.add(t);
        }
        return tList;
    }

    private Put createInsertPut(final String keyValue, final Map<String, String> entityMap) {
        final byte[] rowKey = Bytes.toBytes(keyValue);
        final Put put = new Put(rowKey);
        String columnName;
        String columnValue;
        for (HColumnHandler columnHandler : this.columnHandlerList) {
            columnName = columnHandler.getColumnName();
            columnValue = entityMap.get(columnName);
            if (columnValue == null) {
                this.logger.error("insert H table {} failure message: can not find column:{} value", this.tableName, columnName);
                this.logger.error("insert failure value:{}", entityMap.toString());
                throw new RuntimeException("insert failure message: can not find column value...see log");
            } else {
                put.add(HTableHandler.COLUMN_FAMILY, Bytes.toBytes(columnName), Bytes.toBytes(columnValue));
            }
        }
        return put;
    }

    private Put createUpdatePut(final String keyValue, final Map<String, String> dataMap) {
        final byte[] rowKey = Bytes.toBytes(keyValue);
        final Put put = new Put(rowKey);
        String columnName;
        String columnValue;
        for (HColumnHandler columnHandler : this.columnHandlerList) {
            columnName = columnHandler.getColumnName();
            columnValue = dataMap.get(columnName);
            if (columnValue != null) {
                put.add(HTableHandler.COLUMN_FAMILY, Bytes.toBytes(columnName), Bytes.toBytes(columnValue));
            }
        }
        return put;
    }

    @Override
    public T inquireByKey(String keyValue) {
        T t = null;
        Result result = this.hTableHandler.get(this.tableName, keyValue);
        if (result != null) {
            t = this.readResult(result);
        }
        return t;
    }

    @Override
    public List<T> inquireByKeys(List<String> keyValues) {
        List<T> tList;
        Result[] result = this.hTableHandler.get(tableName, keyValues);
        if (result != null && result.length > 0) {
            tList = this.readResult(result);
        } else {
            tList = new ArrayList<T>(0);
        }
        return tList;
    }

    @Override
    public String insert(Map<String, String> entityMap) {
        final String keyName = this.keyHandler.getColumnName();
        String keyValue = entityMap.get(keyName);
        if (keyValue == null) {
            keyValue = UUID.randomUUID().toString();
            entityMap.put(keyName, keyValue);
        }
        final Put put = this.createInsertPut(keyValue, entityMap);
        this.hTableHandler.put(this.tableName, put);
        return keyValue;
    }

    @Override
    public T insertAndInquire(Map<String, String> entityMap) {
        final String keyName = this.keyHandler.getColumnName();
        String keyValue = entityMap.get(keyName);
        if (keyValue == null) {
            keyValue = UUID.randomUUID().toString();
            entityMap.put(keyName, keyValue);
        }
        final Put put = this.createInsertPut(keyValue, entityMap);
        this.hTableHandler.put(this.tableName, put);
        T t = this.newInstance(entityMap);
        return t;
    }

    @Override
    public void batchInsert(List<Map<String, String>> entityMapList) {
        final String keyName = this.keyHandler.getColumnName();
        List<Put> putList = new ArrayList<Put>(entityMapList.size());
        Put put;
        String keyValue;
        for (Map<String, String> entityMap : entityMapList) {
            keyValue = entityMap.get(keyName);
            if (keyValue == null) {
                keyValue = UUID.randomUUID().toString();
                entityMap.put(keyName, keyValue);
            }
            put = this.createInsertPut(keyValue, entityMap);
            putList.add(put);
        }
        this.hTableHandler.put(tableName, putList);
    }

    @Override
    public String update(Map<String, String> entityMap) {
        final String keyName = this.keyHandler.getColumnName();
        String keyValue = entityMap.get(keyName);
        if (keyValue == null) {
            this.logger.error("update H table {} failure message: can not find key:{} value", this.tableName, keyName);
            this.logger.error("update failure value:{}", entityMap.toString());
            throw new RuntimeException("update failure message: can not find key value...see log");
        } else {
            final Put put = this.createUpdatePut(keyValue, entityMap);
            if (put.isEmpty() == false) {
                this.hTableHandler.put(tableName, put);
            }
        }
        return keyValue;
    }

    @Override
    public void batchUpdate(List<Map<String, String>> entityMapList) {
        final String keyName = this.keyHandler.getColumnName();
        List<Put> putList = new ArrayList<Put>(entityMapList.size());
        Put put;
        String keyValue;
        for (Map<String, String> entityMap : entityMapList) {
            keyValue = entityMap.get(keyName);
            if (keyValue == null) {
                this.logger.error("update H table {} failure message: can not find key:{} value", this.tableName, keyName);
                this.logger.error("update failure value:{}", entityMap.toString());
                throw new RuntimeException("update failure message: can not find key value...see log");
            }
            put = this.createInsertPut(keyValue, entityMap);
            if (put.isEmpty() == false) {
                putList.add(put);
            }
        }
        if (putList.isEmpty() == false) {
            this.hTableHandler.put(this.tableName, putList);
        }
    }

    @Override
    public T updateAndInquire(Map<String, String> entityMap) {
        final String keyName = this.keyHandler.getColumnName();
        String keyValue = entityMap.get(keyName);
        if (keyValue == null) {
            this.logger.error("update H table {} failure message: can not find key:{} value", this.tableName, keyName);
            this.logger.error("update failure value:{}", entityMap.toString());
            throw new RuntimeException("update failure message: can not find key value...see log");
        } else {
            final Put put = this.createUpdatePut(keyValue, entityMap);
            if (put.isEmpty() == false) {
                this.hTableHandler.put(tableName, put);
            }
        }
        return this.inquireByKey(keyValue);
    }

    @Override
    public void delete(String keyValue) {
        this.hTableHandler.delete(this.tableName, keyValue);
    }

    @Override
    public void batchDelete(List<String> keyValues) {
        this.hTableHandler.delete(tableName, keyValues);
    }
}
