package com.wolf.framework.redis;

import com.wolf.framework.dao.annotation.ColumnTypeEnum;
import com.wolf.framework.dao.condition.InquireRedisIndexContext;
import com.wolf.framework.dao.parser.RColumnHandler;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 *
 * @author aladdin
 */
public class RedisHandlerImpl implements RedisHandler {

    private final String tableName;
    private final JedisPool jedisPool;
    private final String connector = "_";
    private final RColumnHandler keyHandler;
    private final List<RColumnHandler> columnHandlerList;
    private final Set<String> indexSet = new HashSet<String>(2, 1);

    public RedisHandlerImpl(String tableName, JedisPool jedisPool, RColumnHandler keyHandler, List<RColumnHandler> columnHandlerList) {
        this.tableName = tableName;
        this.jedisPool = jedisPool;
        this.keyHandler = keyHandler;
        this.columnHandlerList = columnHandlerList;
        for (RColumnHandler rColumnHandler : columnHandlerList) {
            if (rColumnHandler.getColumnType() == ColumnTypeEnum.INDEX) {
                this.indexSet.add(rColumnHandler.getColumnName());
            }
        }
    }

    @Override
    public Map<String, String> inquireByKey(String keyValue) {
        //构造redis key
        StringBuilder strBuilder = new StringBuilder(this.tableName.length() + this.connector.length() + keyValue.length());
        strBuilder.append(this.tableName).append(this.connector).append(keyValue);
        String redisKey = strBuilder.toString();
        //开启连接
        Jedis jedis = this.jedisPool.getResource();
        //查询
        Map<String, String> result = jedis.hgetAll(redisKey);
        if (result.isEmpty() == false ) {
            //保存keyValue
            result.put(this.keyHandler.getColumnName(), keyValue);
        } else {
            result = null;
        }
        //关闭连接
        this.jedisPool.returnResource(jedis);
        return result;
    }

    @Override
    public List<Map<String, String>> inquireBykeys(List<String> keyValueList) {
        List<Map<String, String>> resultList = new ArrayList<Map<String, String>>(keyValueList.size());
        Map<String, String> result;
        String keyName = this.keyHandler.getColumnName();
        //开启连接
        Jedis jedis = this.jedisPool.getResource();
        //查询
        String redisKey;
        StringBuilder strBuilder = new StringBuilder(32);
        for (String keyValue : keyValueList) {
            strBuilder.append(this.tableName).append(this.connector).append(keyValue);
            redisKey = strBuilder.toString();
            strBuilder.setLength(0);
            result = jedis.hgetAll(redisKey);
            if (result != null) {
                result.put(keyName, keyValue);
                resultList.add(result);
            }
        }
        //关闭连接
        this.jedisPool.returnResource(jedis);
        return resultList;
    }

    @Override
    public String insert(Map<String, String> entityMap) {
        final String keyName = this.keyHandler.getColumnName();
        String keyValue = entityMap.get(keyName);
        if (keyValue == null) {
            //如果没有keyValue，自动生成
            keyValue = UUID.randomUUID().toString();
        }
        //构造redis key
        StringBuilder strBuilder = new StringBuilder(32);
        strBuilder.append(this.tableName).append(this.connector).append(keyValue);
        String redisKey = strBuilder.toString();
        strBuilder.setLength(0);
        //开启连接
        Jedis jedis = this.jedisPool.getResource();
        String columnValue;
        String columnName;
        String indexKey;
        //保存每个列的值
        for (RColumnHandler rColumnHandler : this.columnHandlerList) {
            columnName = rColumnHandler.getColumnName();
            columnValue = entityMap.get(columnName);
            if (columnValue != null) {
                //插入
                jedis.hset(redisKey, columnName, columnValue);
                if (rColumnHandler.getColumnType() == ColumnTypeEnum.INDEX) {
                    //如果该列是索引类型，则额外更新列信息
                    strBuilder.append(this.tableName).append(this.connector)
                            .append(columnName).append(this.connector)
                            .append(columnValue);
                    indexKey = strBuilder.toString();
                    strBuilder.setLength(0);
                    jedis.zadd(indexKey, 0, keyValue);
                }
            }
        }
        //关闭
        this.jedisPool.returnResource(jedis);
        return keyValue;
    }

    @Override
    public void batchInsert(List<Map<String, String>> entityMapList) {
        final String keyName = this.keyHandler.getColumnName();
        String keyValue;
        String columnValue;
        String columnName;
        String indexKey;
        String redisKey;
        StringBuilder strBuilder = new StringBuilder(32);
        //开启连接
        Jedis jedis = this.jedisPool.getResource();
        for (Map<String, String> entityMap : entityMapList) {
            keyValue = entityMap.get(keyName);
            if (keyValue == null) {
                //如果没有keyValue，自动生成
                keyValue = UUID.randomUUID().toString();
            }
            //构造redis key
            strBuilder.append(this.tableName).append(this.connector).append(keyValue);
            redisKey = strBuilder.toString();
            strBuilder.setLength(0);
            //保存每个列的值
            for (RColumnHandler rColumnHandler : this.columnHandlerList) {
                columnName = rColumnHandler.getColumnName();
                columnValue = entityMap.get(columnName);
                if (columnValue != null) {
                    //插入
                    jedis.hset(redisKey, columnName, columnValue);
                    if (rColumnHandler.getColumnType() == ColumnTypeEnum.INDEX) {
                        //如果该列是索引类型，则额外更新列信息
                        strBuilder.append(this.tableName).append(this.connector)
                                .append(columnName).append(this.connector)
                                .append(columnValue);
                        indexKey = strBuilder.toString();
                        strBuilder.setLength(0);
                        jedis.zadd(indexKey, 0, keyValue);
                    }
                }
            }
        }
        //关闭连接
        this.jedisPool.returnResource(jedis);
    }

    @Override
    public String update(Map<String, String> entityMap) {
        final String keyName = this.keyHandler.getColumnName();
        String keyValue = entityMap.get(keyName);
        if (keyValue == null) {
            throw new RuntimeException("Can not find keyValue when update:" + entityMap.toString());
        }
        StringBuilder strBuilder = new StringBuilder(32);
        //构造redis key
        strBuilder.append(this.tableName).append(this.connector).append(keyValue);
        String redisKey = strBuilder.toString();
        strBuilder.setLength(0);
        //查询旧记录
        Map<String, String> oldEntityMap = this.inquireByKey(keyValue);
        if (oldEntityMap != null) {
            //比对变化，更新数据及索引
            //开启连接
            Jedis jedis = this.jedisPool.getResource();
            String columnValue;
            String oldColumnValue;
            String columnName;
            String indexKey;
            for (RColumnHandler rColumnHandler : this.columnHandlerList) {
                columnName = rColumnHandler.getColumnName();
                columnValue = entityMap.get(columnName);
                if (columnValue != null) {
                    oldColumnValue = oldEntityMap.get(columnName);
                    if (oldColumnValue == null || oldColumnValue.equals(columnValue) == false) {
                        //值变化，更新
                        jedis.hset(redisKey, columnName, columnValue);
                        if (rColumnHandler.getColumnType() == ColumnTypeEnum.INDEX) {
                            //是索引列
                            if (oldColumnValue != null) {
                                //删除旧索引
                                strBuilder.append(this.tableName).append(this.connector)
                                        .append(columnName).append(this.connector)
                                        .append(oldColumnValue);
                                indexKey = strBuilder.toString();
                                strBuilder.setLength(0);
                                jedis.zrem(indexKey, keyValue);
                            }
                            //更新新索引
                            strBuilder.append(this.tableName).append(this.connector)
                                    .append(columnName).append(this.connector)
                                    .append(columnValue);
                            indexKey = strBuilder.toString();
                            strBuilder.setLength(0);
                            jedis.zadd(indexKey, 0, keyValue);
                        }
                    }
                }
            }
            //关闭连接
            this.jedisPool.returnResource(jedis);
        }
        return keyValue;
    }

    @Override
    public void batchUpdate(List<Map<String, String>> entityMapList) {
        final String keyName = this.keyHandler.getColumnName();
        String keyValue;
        StringBuilder strBuilder = new StringBuilder(32);
        Map<String, String> oldEntityMap;
        String columnValue;
        String oldColumnValue;
        String columnName;
        String indexKey;
        String redisKey;
        //开启连接
        Jedis jedis = this.jedisPool.getResource();
        //
        for (Map<String, String> entityMap : entityMapList) {
            keyValue = entityMap.get(keyName);
            if (keyValue != null) {
                //keyValue 存在
                //构造redis key
                strBuilder.append(this.tableName).append(this.connector).append(keyValue);
                redisKey = strBuilder.toString();
                strBuilder.setLength(0);
                //查询旧记录
                oldEntityMap = this.inquireByKey(keyValue);
                if (oldEntityMap != null) {
                    //比对变化，更新数据及索引
                    for (RColumnHandler rColumnHandler : this.columnHandlerList) {
                        columnName = rColumnHandler.getColumnName();
                        columnValue = entityMap.get(columnName);
                        if (columnValue != null) {
                            oldColumnValue = oldEntityMap.get(columnName);
                            if (oldColumnValue == null || oldColumnValue.equals(columnValue)) {
                                //值变化，更新
                                jedis.hset(redisKey, columnName, columnValue);
                                if (rColumnHandler.getColumnType() == ColumnTypeEnum.INDEX) {
                                    //是索引列
                                    if (oldColumnValue != null) {
                                        //删除旧索引
                                        strBuilder.append(this.tableName).append(this.connector)
                                                .append(columnName).append(this.connector)
                                                .append(oldColumnValue);
                                        indexKey = strBuilder.toString();
                                        strBuilder.setLength(0);
                                        jedis.zrem(indexKey, keyValue);
                                    }
                                    //更新新索引
                                    strBuilder.append(this.tableName).append(this.connector)
                                            .append(columnName).append(this.connector)
                                            .append(columnValue);
                                    indexKey = strBuilder.toString();
                                    strBuilder.setLength(0);
                                    jedis.zadd(indexKey, 0, keyValue);
                                }
                            }
                        }
                    }
                }
            }
        }
        //关闭连接
        this.jedisPool.returnResource(jedis);
    }

    @Override
    public void delete(String keyValue) {
        //构造redis key
        StringBuilder strBuilder = new StringBuilder(this.tableName.length() + this.connector.length() + keyValue.length());
        strBuilder.append(this.tableName).append(this.connector).append(keyValue);
        String redisKey = strBuilder.toString();
        //开启连接
        Jedis jedis = this.jedisPool.getResource();
        //查询
        Map<String, String> entityMap = jedis.hgetAll(keyValue);
        if (entityMap != null) {
            //删除
            jedis.del(redisKey);
            //删除相关索引
            String columnValue;
            String columnName;
            String indexKey;
            //
            for (RColumnHandler rColumnHandler : this.columnHandlerList) {
                columnName = rColumnHandler.getColumnName();
                columnValue = entityMap.get(columnName);
                if (columnValue != null && rColumnHandler.getColumnType() == ColumnTypeEnum.INDEX) {
                    //如果该列是索引类型，则额外更新列信息
                    strBuilder.append(this.tableName).append(this.connector)
                            .append(columnName).append(this.connector)
                            .append(columnValue);
                    indexKey = strBuilder.toString();
                    strBuilder.setLength(0);
                    jedis.zrem(indexKey, keyValue);
                }
            }
        }
        //关闭连接
        this.jedisPool.returnResource(jedis);
    }

    @Override
    public void batchDelete(List<String> keyValueList) {
        StringBuilder strBuilder = new StringBuilder(32);
        String redisKey;
        //开启连接
        Jedis jedis = this.jedisPool.getResource();
        String columnValue;
        String columnName;
        String indexKey;
        Map<String, String> entityMap;
        //删除
        for (String keyValue : keyValueList) {
            strBuilder.append(this.tableName).append(this.connector).append(keyValue);
            redisKey = strBuilder.toString();
            strBuilder.setLength(0);
            //查询
            entityMap = jedis.hgetAll(keyValue);
            if (entityMap != null) {
                //删除
                jedis.del(redisKey);
                //删除索引列
                for (RColumnHandler rColumnHandler : this.columnHandlerList) {
                    columnName = rColumnHandler.getColumnName();
                    columnValue = entityMap.get(columnName);
                    if (columnValue != null && rColumnHandler.getColumnType() == ColumnTypeEnum.INDEX) {
                        //如果该列是索引类型，则额外更新列信息
                        strBuilder.append(this.tableName).append(this.connector)
                                .append(columnName).append(this.connector)
                                .append(columnValue);
                        indexKey = strBuilder.toString();
                        strBuilder.setLength(0);
                        jedis.zrem(indexKey, keyValue);
                    }
                }
            }
        }
        //关闭连接
        this.jedisPool.returnResource(jedis);
    }

    @Override
    public List<String> inquireKeysByIndex(InquireRedisIndexContext inquireRedisIndexContext) {
        String indexName = inquireRedisIndexContext.getIndexName();
        //判断是否存在该索引
        List<String> resultList;
        if (this.indexSet.contains(indexName)) {
            //构造索引redis key
            StringBuilder strBuilder = new StringBuilder(32);
            strBuilder.append(this.tableName).append(this.connector)
                    .append(indexName).append(this.connector).append(inquireRedisIndexContext.getIndexValue());
            String indexKey = strBuilder.toString();
            int pageIndex = inquireRedisIndexContext.getPageIndex();
            int pageSize = inquireRedisIndexContext.getPageSize();
            int start = (pageIndex - 1) * pageSize;
            int end = pageIndex * pageSize - 1;
            //开启连接
            Jedis jedis = this.jedisPool.getResource();
            Set<String> keySet = jedis.zrange(indexKey, start, end);
            resultList = new ArrayList<String>(keySet.size());
            resultList.addAll(keySet);
            //关闭连接
            this.jedisPool.returnResource(jedis);
        } else {
            throw new RuntimeException("Error when inquire by index. Cause:can not find index name:" + indexName);
        }
        return resultList;
    }

    @Override
    public long countByIndex(String indexName, String indexValue) {
        long result;
        if (this.indexSet.contains(indexName)) {
            //构造索引redis key
            StringBuilder strBuilder = new StringBuilder(32);
            strBuilder.append(this.tableName).append(this.connector)
                    .append(indexName).append(this.connector).append(indexValue);
            String indexKey = strBuilder.toString();
            //开启连接
            Jedis jedis = this.jedisPool.getResource();
            result = jedis.zcard(indexKey);
            //关闭连接
            this.jedisPool.returnResource(jedis);
        } else {
            throw new RuntimeException("Error when count by index. Cause:can not find index name:" + indexName);
        }
        return result;
    }
}
