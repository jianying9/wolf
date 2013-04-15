package com.wolf.framework.hbase;

import com.wolf.framework.config.FrameworkLoggerEnum;
import com.wolf.framework.logger.LogFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;

/**
 *
 * @author aladdin
 */
public final class HTableHandlerImpl implements HTableHandler {

    private final Configuration config;
    private final HTablePool hTablePool;
    private final Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.DAO);

    public HTableHandlerImpl(Configuration config) {
        this.config = config;
        this.hTablePool = new HTablePool(this.config, 1);
//        this.hTablePool = new HTablePool(this.config, 2, PoolType.ThreadLocal);
    }

    private HTableInterface getHTable(String tableName) {
        return this.hTablePool.getTable(tableName);
    }

    @Override
    public void put(String tableName, Put put) {
        HTableInterface hTableInterface = this.getHTable(tableName);
        try {
            hTableInterface.put(put);
        } catch (IOException ex) {
            this.logger.error("HBase put table:{} error", tableName);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void put(String tableName, List<Put> putList) {
        HTableInterface hTableInterface = this.getHTable(tableName);
        try {
            hTableInterface.put(putList);
        } catch (IOException ex) {
            this.logger.error("HBase list put table:{} error", tableName);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void delete(String tableName, String keyValue) {
        byte[] rowKey = Bytes.toBytes(keyValue);
        Delete delete = new Delete(rowKey);
        HTableInterface hTableInterface = this.getHTable(tableName);
        try {
            hTableInterface.delete(delete);
        } catch (IOException ex) {
            this.logger.error("HBase delete table:{},keyValue:{} error", tableName, keyValue);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void delete(String tableName, List<String> keyValues) {
        byte[] rowKey;
        Delete delete;
        List<Delete> deleteList = new ArrayList<Delete>(keyValues.size());
        for (String keyValue : keyValues) {
            rowKey = Bytes.toBytes(keyValue);
            delete = new Delete(rowKey);
            deleteList.add(delete);
        }
        HTableInterface hTableInterface = this.getHTable(tableName);
        try {
            hTableInterface.delete(deleteList);
        } catch (IOException ex) {
            this.logger.error("HBase list delete table:{} error", tableName);
            for (String keyValue : keyValues) {
                this.logger.error("----delete keyValue:{}", keyValue);
            }
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Result get(String tableName, String keyValue) {
        Result result = null;
        byte[] rowKey = Bytes.toBytes(keyValue);
        Get get = new Get(rowKey);
        get.setMaxVersions();
        HTableInterface hTableInterface = this.getHTable(tableName);
        try {
            result = hTableInterface.get(get);
        } catch (IOException ex) {
            this.logger.error("HBase get table:{},keyValue:{} error", tableName, keyValue);
            throw new RuntimeException(ex);
        }
        return result;
    }

    @Override
    public Result[] get(String tableName, List<String> keyValues) {
        Result[] result = null;
        byte[] rowKey;
        Get get;
        List<Get> getList = new ArrayList<Get>(keyValues.size());
        for (String keyValue : keyValues) {
            rowKey = Bytes.toBytes(keyValue);
            get = new Get(rowKey);
            get.setMaxVersions();
            getList.add(get);
        }
        HTableInterface hTableInterface = this.getHTable(tableName);
        try {
            result = hTableInterface.get(getList);
        } catch (IOException ex) {
            this.logger.error("HBase list get table:{},keyValue size:{} error", tableName, keyValues.size());
            for (String keyValue : keyValues) {
                this.logger.error("----get keyValue:{}", keyValue);
            }
            throw new RuntimeException(ex);
        }
        return result;
    }

    @Override
    public List<Result> scan(String tableName, Scan scan) {
        List<Result> resultList;
        ResultScanner rs = null;
        scan.setBatch(100);
        scan.setMaxVersions();
        HTableInterface hTableInterface = this.getHTable(tableName);
        try {
            rs = hTableInterface.getScanner(scan);
            resultList = new ArrayList<Result>(20);
            Result result = rs.next();
            while (result != null) {
                resultList.add(result);
                result = rs.next();
            }
        } catch (IOException ex) {
            this.logger.error("HBase scan table:{} error", tableName);
            throw new RuntimeException(ex);
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
        return resultList;
    }

    @Override
    public List<String> scanRowKey(String tableName, Scan scan) {
        List<String> resultList;
        ResultScanner rs = null;
        byte[] rowKey;
        scan.setBatch(100);
        scan.setMaxVersions();
        HTableInterface hTableInterface = this.getHTable(tableName);
        try {
            rs = hTableInterface.getScanner(scan);
            resultList = new ArrayList<String>(20);
            Result result = rs.next();
            while (result != null) {
                rowKey = result.getRow();
                resultList.add(Bytes.toString(rowKey));
                result = rs.next();
            }
        } catch (IOException ex) {
            this.logger.error("HBase scan table for rowKey:{} error", tableName);
            throw new RuntimeException(ex);
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
        return resultList;
    }

    @Override
    public boolean isTableExists(String tableName) {
        boolean result;
        try {
            HBaseAdmin hbaseAdmin = new HBaseAdmin(config);
            result = hbaseAdmin.tableExists(tableName);
        } catch (IOException ex) {
            this.logger.error("HBase assert table exists:{} error", tableName);
            throw new RuntimeException(ex);
        }
        return result;
    }

    @Override
    public void createTable(String tableName) {
        HTableDescriptor hTableDescriptor = new HTableDescriptor(tableName);
        byte[] columnFamily = Bytes.toBytes(tableName.toLowerCase());
        HColumnDescriptor columnFamilyDescriptor = new HColumnDescriptor(columnFamily);
        hTableDescriptor.addFamily(columnFamilyDescriptor);
        try {
            HBaseAdmin hbaseAdmin = new HBaseAdmin(config);
            hbaseAdmin.createTable(hTableDescriptor);
        } catch (IOException ex) {
            this.logger.error("HBase create table:{} error", tableName);
            throw new RuntimeException(ex);
        }
    }
}
