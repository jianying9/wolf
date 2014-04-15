package com.wolf.framework.redis;

import com.wolf.framework.dao.annotation.ColumnTypeEnum;
import com.wolf.framework.dao.condition.InquirePageContext;
import com.wolf.framework.dao.condition.InquireRedisIndexContext;
import com.wolf.framework.dao.parser.RColumnHandler;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private final String keyIndexPrefix = "KEY";
    private final String columnIndexPrefix = "INDEX";
    private final RColumnHandler keyHandler;
    private final List<RColumnHandler> columnHandlerList;
    private final Set<String> indexColumnNameSet = new HashSet<String>(2, 1);

    public RedisHandlerImpl(String tableName, JedisPool jedisPool, RColumnHandler keyHandler, List<RColumnHandler> columnHandlerList) {
        this.tableName = tableName;
        this.jedisPool = jedisPool;
        this.keyHandler = keyHandler;
        this.columnHandlerList = columnHandlerList;
        for (RColumnHandler rColumnHandler : columnHandlerList) {
            if (rColumnHandler.getColumnType() == ColumnTypeEnum.INDEX) {
                this.indexColumnNameSet.add(rColumnHandler.getColumnName());
            }
        }
    }

    @Override
    public String getKeyIndexName() {
        StringBuilder strBuilder = new StringBuilder(32);
        strBuilder.append(this.keyIndexPrefix).append(this.connector).append(this.tableName);
        return strBuilder.toString();
    }

    @Override
    public String getColumnIndexName(String columnName, String columnValue) {
        StringBuilder strBuilder = new StringBuilder(32);
        strBuilder.append(this.columnIndexPrefix).append(this.connector)
                .append(this.tableName).append(this.connector)
                .append(columnName).append(this.connector)
                .append(columnValue);
        return strBuilder.toString();
    }

    @Override
    public Map<String, String> inquireByKey(String keyValue) {
        //构造redis key
        StringBuilder strBuilder = new StringBuilder(this.tableName.length() + this.connector.length() + keyValue.length());
        strBuilder.append(this.tableName).append(this.connector).append(keyValue);
        String redisRowKey = strBuilder.toString();
        Map<String, String> result = null;
        //开启连接
        Jedis jedis = this.jedisPool.getResource();
        try {
            //查询
            result = jedis.hgetAll(redisRowKey);
            if (result.isEmpty()) {
                //redis key 不存在
                result = null;
            } else {
                //redis key 存在，保存keyValue
                result.put(this.keyHandler.getColumnName(), keyValue);
            }
        } finally {
            //关闭连接
            this.jedisPool.returnResource(jedis);
        }
        return result;
    }

    @Override
    public List<Map<String, String>> inquireBykeys(List<String> keyValueList) {
        List<Map<String, String>> resultList = new ArrayList<Map<String, String>>(keyValueList.size());
        Map<String, String> result;
        String keyName = this.keyHandler.getColumnName();
        String redisRowKey;
        StringBuilder strBuilder = new StringBuilder(32);
        strBuilder.append(this.tableName).append(this.connector);
        final int prefixLength = strBuilder.length();
        //开启连接
        Jedis jedis = this.jedisPool.getResource();
        try {
            //查询
            for (String keyValue : keyValueList) {
                strBuilder.append(keyValue);
                redisRowKey = strBuilder.toString();
                strBuilder.setLength(prefixLength);
                result = jedis.hgetAll(redisRowKey);
                if (result.isEmpty() == false) {
                    //redis key 存在，保存keyValue
                    result.put(keyName, keyValue);
                    resultList.add(result);
                }
            }
        } finally {
            //关闭连接
            this.jedisPool.returnResource(jedis);
        }
        return resultList;
    }

    @Override
    public String insert(Map<String, String> entityMap) {
        final String keyName = this.keyHandler.getColumnName();
        String keyValue = entityMap.get(keyName);
        if (keyValue == null) {
            throw new RuntimeException("Can not find keyValue when insert:" + entityMap.toString());
        }
        //构造redis 记录key
        StringBuilder strBuilder = new StringBuilder(this.tableName.length() + this.connector.length() + keyValue.length());
        strBuilder.append(this.tableName).append(this.connector).append(keyValue);
        String redisRowKey = strBuilder.toString();
        strBuilder.setLength(0);
        //构造redis 主键 index key
        strBuilder.append(this.keyIndexPrefix).append(this.connector).append(this.tableName);
        String redisIndexKey = strBuilder.toString();
        strBuilder.setLength(0);
        String columnValue;
        String columnName;
        String redisColumnIndexKey;
        String score;
        long defaultScore = System.currentTimeMillis();
        //开启连接
        Jedis jedis = this.jedisPool.getResource();
        try {
            //保存每个列的值
            for (RColumnHandler rColumnHandler : this.columnHandlerList) {
                columnName = rColumnHandler.getColumnName();
                columnValue = entityMap.get(columnName);
                if (columnValue != null) {
                    //插入
                    jedis.hset(redisRowKey, columnName, columnValue);
                    if (rColumnHandler.getColumnType() == ColumnTypeEnum.INDEX) {
                        //如果该列是索引类型，则额外更新列信息
                        strBuilder.append(this.columnIndexPrefix).append(this.connector)
                                .append(this.tableName).append(this.connector)
                                .append(columnName).append(this.connector)
                                .append(columnValue);
                        redisColumnIndexKey = strBuilder.toString();
                        strBuilder.setLength(0);
                        score = entityMap.get(redisColumnIndexKey);
                        if (score == null) {
                            jedis.zadd(redisColumnIndexKey, defaultScore, keyValue);
                        } else {
                            jedis.zadd(redisColumnIndexKey, Long.parseLong(score), keyValue);
                        }
                    }
                }
            }
            //保存key索引
            score = entityMap.get(redisIndexKey);
            if (score == null) {
                jedis.zadd(redisIndexKey, defaultScore, keyValue);
            } else {
                jedis.zadd(redisIndexKey, Long.parseLong(score), keyValue);
            }
        } finally {
            //关闭
            this.jedisPool.returnResource(jedis);
        }
        return keyValue;
    }

    @Override
    public void batchInsert(List<Map<String, String>> entityMapList) {
        final String keyName = this.keyHandler.getColumnName();
        String keyValue;
        String columnValue;
        String columnName;
        String redisColumnIndexKey;
        String redisRowKey;
        String score;
        long defaultScore;
        StringBuilder strBuilder = new StringBuilder(32);
        //开启连接
        Jedis jedis = this.jedisPool.getResource();
        try {
            for (Map<String, String> entityMap : entityMapList) {
                keyValue = entityMap.get(keyName);
                if (keyValue == null) {
                    throw new RuntimeException("Can not find keyValue when insert:" + entityMap.toString());
                }
                //构造redis 记录key
                strBuilder.append(this.tableName).append(this.connector).append(keyValue);
                redisRowKey = strBuilder.toString();
                strBuilder.setLength(0);
                //构造redis 主键 index key
                strBuilder.append(this.keyIndexPrefix).append(this.connector).append(this.tableName);
                String redisIndexKey = strBuilder.toString();
                strBuilder.setLength(0);
                //保存每个列的值
                defaultScore = System.currentTimeMillis();
                for (RColumnHandler rColumnHandler : this.columnHandlerList) {
                    columnName = rColumnHandler.getColumnName();
                    columnValue = entityMap.get(columnName);
                    if (columnValue != null) {
                        //插入
                        jedis.hset(redisRowKey, columnName, columnValue);
                        if (rColumnHandler.getColumnType() == ColumnTypeEnum.INDEX) {
                            //如果该列是索引类型，则额外更新列信息
                            strBuilder.append(this.columnIndexPrefix).append(this.connector)
                                    .append(this.tableName).append(this.connector)
                                    .append(columnName).append(this.connector)
                                    .append(columnValue);
                            redisColumnIndexKey = strBuilder.toString();
                            strBuilder.setLength(0);
                            score = entityMap.get(redisColumnIndexKey);
                            if (score == null) {
                                jedis.zadd(redisColumnIndexKey, defaultScore, keyValue);
                            } else {
                                jedis.zadd(redisColumnIndexKey, Long.parseLong(score), keyValue);
                            }
                        }
                    }
                }
                //保存key索引
                score = entityMap.get(redisIndexKey);
                if (score == null) {
                    jedis.zadd(redisIndexKey, defaultScore, keyValue);
                } else {
                    jedis.zadd(redisIndexKey, Long.parseLong(score), keyValue);
                }
            }
        } finally {
            //关闭连接
            this.jedisPool.returnResource(jedis);
        }
    }

    @Override
    public String update(Map<String, String> entityMap) {
        final String keyName = this.keyHandler.getColumnName();
        String keyValue = entityMap.get(keyName);
        if (keyValue == null) {
            throw new RuntimeException("Can not find keyValue when update:" + entityMap.toString());
        }
        StringBuilder strBuilder = new StringBuilder(this.tableName.length() + this.connector.length() + keyValue.length());
        //构造redis 记录key
        strBuilder.append(this.tableName).append(this.connector).append(keyValue);
        String redisKey = strBuilder.toString();
        strBuilder.setLength(0);
        //查询旧记录
        Map<String, String> oldEntityMap = this.inquireByKey(keyValue);
        if (oldEntityMap != null) {
            //比对变化，更新数据及索引
            String columnValue;
            String oldColumnValue;
            String columnName;
            String redisColumnIndexKey;
            String score;
            long defaultScore = System.currentTimeMillis();
            //开启连接
            Jedis jedis = this.jedisPool.getResource();
            try {
                for (RColumnHandler rColumnHandler : this.columnHandlerList) {
                    columnName = rColumnHandler.getColumnName();
                    columnValue = entityMap.get(columnName);
                    if (columnValue != null) {
                        oldColumnValue = oldEntityMap.get(columnName);
                        if (oldColumnValue == null || oldColumnValue.equals(columnValue) == false) {
                            //值变化，更新
                            jedis.hset(redisKey, columnName, columnValue);
                        }
                        if (rColumnHandler.getColumnType() == ColumnTypeEnum.INDEX) {
                            //是索引列
                            if (oldColumnValue != null && oldColumnValue.equals(columnValue) == false) {
                                //删除旧索引
                                strBuilder.append(this.columnIndexPrefix).append(this.connector)
                                        .append(this.tableName).append(this.connector)
                                        .append(columnName).append(this.connector)
                                        .append(oldColumnValue);
                                redisColumnIndexKey = strBuilder.toString();
                                strBuilder.setLength(0);
                                jedis.zrem(redisColumnIndexKey, keyValue);
                            }
                            //更新新索引
                            strBuilder.append(this.columnIndexPrefix).append(this.connector)
                                    .append(this.tableName).append(this.connector)
                                    .append(columnName).append(this.connector)
                                    .append(columnValue);
                            redisColumnIndexKey = strBuilder.toString();
                            strBuilder.setLength(0);
                            score = entityMap.get(redisColumnIndexKey);
                            if (score != null) {
                                jedis.zadd(redisColumnIndexKey, Long.parseLong(score), keyValue);
                            } else {
                                if (oldColumnValue == null || oldColumnValue.equals(columnValue) == false) {
                                    jedis.zadd(redisColumnIndexKey, defaultScore, keyValue);
                                }
                            }
                        }
                    }
                }
                //构造redis 主键 index key
                strBuilder.append(this.keyIndexPrefix).append(this.connector).append(this.tableName);
                String redisIndexKey = strBuilder.toString();
                strBuilder.setLength(0);
                //保存key索引
                score = entityMap.get(redisIndexKey);
                if (score != null) {
                    jedis.zadd(redisIndexKey, Long.parseLong(score), keyValue);
                }
            } finally {
                //关闭连接
                this.jedisPool.returnResource(jedis);
            }
        } else {
            keyValue = "";
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
        String redisIndexKey;
        String redisColumnIndexKey;
        String redisRowKey;
        String score;
        long defaultScore = System.currentTimeMillis();
        //开启连接
        Jedis jedis = this.jedisPool.getResource();
        try {
            for (Map<String, String> entityMap : entityMapList) {
                keyValue = entityMap.get(keyName);
                if (keyValue != null) {
                    //keyValue 存在
                    //构造redis 记录key
                    strBuilder.append(this.tableName).append(this.connector).append(keyValue);
                    redisRowKey = strBuilder.toString();
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
                                if (oldColumnValue == null || oldColumnValue.equals(columnValue) == false) {
                                    //值变化，更新
                                    jedis.hset(redisRowKey, columnName, columnValue);
                                }
                                if (rColumnHandler.getColumnType() == ColumnTypeEnum.INDEX) {
                                    //是索引列
                                    if (oldColumnValue != null && oldColumnValue.equals(columnValue) == false) {
                                        //删除旧索引
                                        strBuilder.append(this.columnIndexPrefix).append(this.connector)
                                                .append(this.tableName).append(this.connector)
                                                .append(columnName).append(this.connector)
                                                .append(oldColumnValue);
                                        redisColumnIndexKey = strBuilder.toString();
                                        strBuilder.setLength(0);
                                        jedis.zrem(redisColumnIndexKey, keyValue);
                                    }
                                    //更新新索引
                                    strBuilder.append(this.columnIndexPrefix).append(this.connector)
                                            .append(this.tableName).append(this.connector)
                                            .append(columnName).append(this.connector)
                                            .append(columnValue);
                                    redisColumnIndexKey = strBuilder.toString();
                                    strBuilder.setLength(0);
                                    score = entityMap.get(redisColumnIndexKey);
                                    if (score != null) {
                                        jedis.zadd(redisColumnIndexKey, Long.parseLong(score), keyValue);
                                    } else {
                                        if (oldColumnValue == null || oldColumnValue.equals(columnValue) == false) {
                                            jedis.zadd(redisColumnIndexKey, defaultScore, keyValue);
                                        }
                                    }
                                }
                            }
                        }
                        //构造redis 主键 index key
                        strBuilder.append(this.keyIndexPrefix).append(this.connector).append(this.tableName);
                        redisIndexKey = strBuilder.toString();
                        strBuilder.setLength(0);
                        //保存key索引
                        score = entityMap.get(redisIndexKey);
                        if (score != null) {
                            jedis.zadd(redisIndexKey, Long.parseLong(score), keyValue);
                        }
                    }
                }
            }
        } finally {
            //关闭连接
            this.jedisPool.returnResource(jedis);
        }
    }

    @Override
    public void delete(String keyValue) {
        //构造redis 记录key
        StringBuilder strBuilder = new StringBuilder(this.tableName.length() + this.connector.length() + keyValue.length());
        strBuilder.append(this.tableName).append(this.connector).append(keyValue);
        String redisRowKey = strBuilder.toString();
        strBuilder.setLength(0);
        //构造redis 主键 index key
        strBuilder.append(this.keyIndexPrefix).append(this.connector).append(this.tableName);
        String redisIndexKey = strBuilder.toString();
        strBuilder.setLength(0);
        //开启连接
        Jedis jedis = this.jedisPool.getResource();
        try {
            //查询
            Map<String, String> entityMap = jedis.hgetAll(keyValue);
            if (entityMap != null) {
                //删除
                jedis.del(redisRowKey);
                //删除相关索引
                String columnValue;
                String columnName;
                String redisColumnIndexKey;
                //
                for (RColumnHandler rColumnHandler : this.columnHandlerList) {
                    columnName = rColumnHandler.getColumnName();
                    columnValue = entityMap.get(columnName);
                    if (columnValue != null && rColumnHandler.getColumnType() == ColumnTypeEnum.INDEX) {
                        //如果该列是索引类型，则额外更新列信息
                        strBuilder.append(this.columnIndexPrefix).append(this.connector)
                                .append(this.tableName).append(this.connector)
                                .append(columnName).append(this.connector)
                                .append(columnValue);
                        redisColumnIndexKey = strBuilder.toString();
                        strBuilder.setLength(0);
                        jedis.zrem(redisColumnIndexKey, keyValue);
                    }
                }
                //删除主键索引
                jedis.zrem(redisIndexKey, keyValue);
            }
        } finally {
            //关闭连接
            this.jedisPool.returnResource(jedis);
        }
    }

    @Override
    public void batchDelete(List<String> keyValueList) {
        StringBuilder strBuilder = new StringBuilder(32);
        String redisRowKey;
        String columnValue;
        String columnName;
        String redisColumnIndexKey;
        Map<String, String> entityMap;
        //开启连接
        Jedis jedis = this.jedisPool.getResource();
        try {
            //删除
            for (String keyValue : keyValueList) {
                //构造redis 记录key
                strBuilder.append(this.tableName).append(this.connector).append(keyValue);
                redisRowKey = strBuilder.toString();
                strBuilder.setLength(0);
                //构造redis 主键 index key
                strBuilder.append(this.keyIndexPrefix).append(this.connector).append(this.tableName);
                String redisIndexKey = strBuilder.toString();
                strBuilder.setLength(0);
                //查询
                entityMap = jedis.hgetAll(keyValue);
                if (entityMap != null) {
                    //删除
                    jedis.del(redisRowKey);
                    //删除索引列
                    for (RColumnHandler rColumnHandler : this.columnHandlerList) {
                        columnName = rColumnHandler.getColumnName();
                        columnValue = entityMap.get(columnName);
                        if (columnValue != null && rColumnHandler.getColumnType() == ColumnTypeEnum.INDEX) {
                            //如果该列是索引类型，则额外更新列信息
                            strBuilder.append(this.columnIndexPrefix).append(this.connector)
                                    .append(this.tableName).append(this.connector)
                                    .append(columnName).append(this.connector)
                                    .append(columnValue);
                            redisColumnIndexKey = strBuilder.toString();
                            strBuilder.setLength(0);
                            jedis.zrem(redisColumnIndexKey, keyValue);
                        }
                    }
                    //删除主键索引
                    jedis.zrem(redisIndexKey, keyValue);
                }
            }
        } finally {
            //关闭连接
            this.jedisPool.returnResource(jedis);
        }
    }

    @Override
    public List<String> inquireKeys(InquirePageContext inquirePageContext) {
        List<String> resultList;
        StringBuilder strBuilder = new StringBuilder(32);
        //构造redis 主键 index key
        strBuilder.append(this.keyIndexPrefix).append(this.connector).append(this.tableName);
        String redisIndexKey = strBuilder.toString();
        strBuilder.setLength(0);
        long pageIndex = inquirePageContext.getPageIndex();
        long pageSize = inquirePageContext.getPageSize();
        long start = (pageIndex - 1) * pageSize;
        long end = pageIndex * pageSize - 1;
        //开启连接
        Jedis jedis = this.jedisPool.getResource();
        try {
            Set<String> keySet = jedis.zrange(redisIndexKey, start, end);
            resultList = new ArrayList<String>(keySet.size());
            resultList.addAll(keySet);
        } finally {
            //关闭连接
            this.jedisPool.returnResource(jedis);
        }
        return resultList;
    }

    @Override
    public List<String> inquireKeysDESC(InquirePageContext inquirePageContext) {
        List<String> resultList;
        StringBuilder strBuilder = new StringBuilder(32);
        //构造redis 主键 index key
        strBuilder.append(this.keyIndexPrefix).append(this.connector).append(this.tableName);
        String redisIndexKey = strBuilder.toString();
        strBuilder.setLength(0);
        long pageIndex = inquirePageContext.getPageIndex();
        long pageSize = inquirePageContext.getPageSize();
        long start = (pageIndex - 1) * pageSize;
        long end = pageIndex * pageSize - 1;
        //开启连接
        Jedis jedis = this.jedisPool.getResource();
        try {
            Set<String> keySet = jedis.zrevrange(redisIndexKey, start, end);
            resultList = new ArrayList<String>(keySet.size());
            resultList.addAll(keySet);
        } finally {
            //关闭连接
            this.jedisPool.returnResource(jedis);
        }
        return resultList;
    }

    @Override
    public long count() {
        long result;
        //构造redis 主键 index key
        StringBuilder strBuilder = new StringBuilder(32);
        strBuilder.append(this.keyIndexPrefix).append(this.connector).append(this.tableName);
        String redisIndexKey = strBuilder.toString();
        //开启连接
        Jedis jedis = this.jedisPool.getResource();
        try {
            result = jedis.zcard(redisIndexKey);
        } finally {
            //关闭连接
            this.jedisPool.returnResource(jedis);
        }
        return result;
    }

    @Override
    public List<String> inquireKeysByIndex(InquireRedisIndexContext inquireRedisIndexContext) {
        String indexName = inquireRedisIndexContext.getIndexName();
        //判断是否存在该索引
        List<String> resultList;
        if (this.indexColumnNameSet.contains(indexName)) {
            //构造redis 列索引key
            StringBuilder strBuilder = new StringBuilder(32);
            strBuilder.append(this.columnIndexPrefix).append(this.connector)
                    .append(this.tableName).append(this.connector)
                    .append(indexName).append(this.connector)
                    .append(inquireRedisIndexContext.getIndexValue());
            String redisColumnIndexKey = strBuilder.toString();
            long pageIndex = inquireRedisIndexContext.getPageIndex();
            long pageSize = inquireRedisIndexContext.getPageSize();
            long start = (pageIndex - 1) * pageSize;
            long end = pageIndex * pageSize - 1;
            //开启连接
            Jedis jedis = this.jedisPool.getResource();
            try {
                Set<String> keySet = jedis.zrange(redisColumnIndexKey, start, end);
                resultList = new ArrayList<String>(keySet.size());
                resultList.addAll(keySet);
            } finally {
                //关闭连接
                this.jedisPool.returnResource(jedis);
            }
        } else {
            throw new RuntimeException("Error when inquire by index. Cause:can not find index name:" + indexName);
        }
        return resultList;
    }

    @Override
    public List<String> inquireKeysByIndexDESC(InquireRedisIndexContext inquireRedisIndexContext) {
        String indexName = inquireRedisIndexContext.getIndexName();
        //判断是否存在该索引
        List<String> resultList;
        if (this.indexColumnNameSet.contains(indexName)) {
            //构造redis 列索引key
            StringBuilder strBuilder = new StringBuilder(32);
            strBuilder.append(this.columnIndexPrefix).append(this.connector)
                    .append(this.tableName).append(this.connector)
                    .append(indexName).append(this.connector)
                    .append(inquireRedisIndexContext.getIndexValue());
            String redisColumnIndexKey = strBuilder.toString();
            long pageIndex = inquireRedisIndexContext.getPageIndex();
            long pageSize = inquireRedisIndexContext.getPageSize();
            long start = (pageIndex - 1) * pageSize;
            long end = pageIndex * pageSize - 1;
            //开启连接
            Jedis jedis = this.jedisPool.getResource();
            try {
                Set<String> keySet = jedis.zrevrange(redisColumnIndexKey, start, end);
                resultList = new ArrayList<String>(keySet.size());
                resultList.addAll(keySet);
            } finally {
                //关闭连接
                this.jedisPool.returnResource(jedis);
            }
        } else {
            throw new RuntimeException("Error when inquire by index. Cause:can not find index name:" + indexName);
        }
        return resultList;
    }

    @Override
    public long countByIndex(String indexName, String indexValue) {
        long result;
        if (this.indexColumnNameSet.contains(indexName)) {
            //构造索引redis key
            StringBuilder strBuilder = new StringBuilder(32);
            strBuilder.append(this.columnIndexPrefix).append(this.connector)
                    .append(this.tableName).append(this.connector)
                    .append(indexName).append(this.connector).append(indexValue);
            String indexKey = strBuilder.toString();
            //开启连接
            Jedis jedis = this.jedisPool.getResource();
            try {
                result = jedis.zcard(indexKey);
            } finally {
                //关闭连接
                this.jedisPool.returnResource(jedis);
            }
        } else {
            throw new RuntimeException("Error when count by index. Cause:can not find index name:" + indexName);
        }
        return result;
    }

    @Override
    public long increase(String keyValue, String columnName, long value) {
        long result;
        if (this.indexColumnNameSet.contains(columnName)) {
            throw new RuntimeException("Error when increase. Cause:can not operate index column:" + columnName);
        }
        //构造redis 记录key
        StringBuilder strBuilder = new StringBuilder(this.tableName.length() + this.connector.length() + keyValue.length());
        strBuilder.append(this.tableName).append(this.connector).append(keyValue);
        String redisRowKey = strBuilder.toString();
        strBuilder.setLength(0);
        //开启连接
        Jedis jedis = this.jedisPool.getResource();
        try {
            result = jedis.hincrBy(redisRowKey, columnName, value);
        } finally {
            //关闭连接
            this.jedisPool.returnResource(jedis);
        }
        return result;
    }

    @Override
    public void updateKeySorce(String keyValue, long sorce) {
        StringBuilder strBuilder = new StringBuilder(this.tableName.length() + this.connector.length() + this.keyIndexPrefix.length() + keyValue.length());
        //构造redis 主键 index key
        strBuilder.append(this.keyIndexPrefix).append(this.connector).append(this.tableName);
        String redisIndexKey = strBuilder.toString();
        strBuilder.setLength(0);
        //开启连接
        Jedis jedis = this.jedisPool.getResource();
        try {
            //保存key索引
            jedis.zadd(redisIndexKey, sorce, keyValue);
        } finally {
            //关闭连接
            this.jedisPool.returnResource(jedis);
        }
    }
}
