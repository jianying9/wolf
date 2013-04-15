package com.wolf.framework.hbase;

import java.util.List;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;

/**
 *
 * @author aladdin
 */
public interface HTableHandler {

    public void put(String tableName, Put put);

    public void put(String tableName, List<Put> putList);

    public void delete(String tableName, String keyValue);

    public void delete(String tableName, List<String> keyValues);

    public Result get(String tableName, String keyValue);

    public Result[] get(String tableName, List<String> keyValues);

    public List<Result> scan(String tableName, Scan scan);
    
    public List<String> scanRowKey(String tableName, Scan scan);

    public boolean isTableExists(String tableName);

    public void createTable(String tableName);
}
