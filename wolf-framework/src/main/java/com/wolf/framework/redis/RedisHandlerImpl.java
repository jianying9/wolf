package com.wolf.framework.redis;

import com.wolf.framework.dao.annotation.ColumnTypeEnum;
import com.wolf.framework.dao.condition.InquirePageContext;
import com.wolf.framework.dao.condition.InquireIndexPageContext;
import com.wolf.framework.dao.parser.RColumnHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisDataException;

/**
 *
 * @author aladdin
 */
public final class RedisHandlerImpl implements RedisHandler {

    private final int dbIndex;
    private final JedisPool jedisPool;
    private final String connector = "_";
    private final String tableIndexKey;
    private final String columnIndexKeyPrefix;
    private final String sortedSetPrefix;
    private final RColumnHandler keyHandler;
    private final List<RColumnHandler> columnHandlerList;
    private final Set<String> sortedSetNames;
    private final Set<String> indexColumnNameSet = new HashSet<String>(2, 1);

    public RedisHandlerImpl(String tableName, JedisPool jedisPool, RColumnHandler keyHandler, List<RColumnHandler> columnHandlerList, Set<String> sortedSetNames) {
        this.jedisPool = jedisPool;
        this.keyHandler = keyHandler;
        this.columnHandlerList = columnHandlerList;
        for (RColumnHandler rColumnHandler : columnHandlerList) {
            if (rColumnHandler.getColumnType() == ColumnTypeEnum.INDEX) {
                this.indexColumnNameSet.add(rColumnHandler.getColumnName());
            }
        }
        this.sortedSetNames = sortedSetNames;
        this.tableIndexKey = "KEY" + this.connector + tableName;
        this.columnIndexKeyPrefix = "INDEX" + this.connector + tableName + this.connector;
        this.sortedSetPrefix = "SORTED_SET" + this.connector;
        //获取dbindex
        Jedis jedis = this.jedisPool.getResource();
        try {
            jedis.select(0);
            String dbIndexStr = jedis.hget(RedisHandler.META_DBINDEX, tableName);
            if (dbIndexStr == null) {
                //未分配
                long nextDbIndex = jedis.hincrBy(RedisHandler.META_SEQUENCE, RedisHandler.SEQUENCE_DBINDEX, 1);
                this.dbIndex = (int) nextDbIndex;
                jedis.hset(RedisHandler.META_DBINDEX, tableName, Integer.toString(this.dbIndex));
            } else {
                //已分配
                this.dbIndex = Integer.parseInt(dbIndexStr);
            }
            //验证dbindex
            jedis.select(this.dbIndex);
        } catch (JedisDataException ex) {
            System.err.println(ex);
            throw new RuntimeException("Error when init redis dao.entityName:" + tableName + " dbindex:" + this.dbIndex + " invalid dbIndex!");
        } finally {
            this.jedisPool.returnResource(jedis);
        }
    }

    @Override
    public String getTableIndexKey() {
        return this.tableIndexKey;
    }

    @Override
    public String getColumnIndexKey(String columnName, String columnValue) {
        StringBuilder strBuilder = new StringBuilder(32);
        strBuilder.append(this.columnIndexKeyPrefix).append(columnName)
                .append(this.connector).append(columnValue);
        return strBuilder.toString();
    }

    @Override
    public boolean exist(String keyValue) {
        boolean result = false;
        //开启连接
        Jedis jedis = this.jedisPool.getResource();
        try {
            //查询
            jedis.select(0);
            Double score = jedis.zscore(this.tableIndexKey, keyValue);
            if (score != null) {
                result = true;
            }
        } finally {
            //关闭连接
            this.jedisPool.returnResource(jedis);
        }
        return result;
    }

    private Map<String, String> localInquireByKey(String keyValue, Jedis jedis) {
        Map<String, String> result = null;
        //查询
        jedis.select(0);
        Double score = jedis.zscore(this.tableIndexKey, keyValue);
        if (score != null) {
            jedis.select(this.dbIndex);
            result = jedis.hgetAll(keyValue);
            //redis key 存在，保存keyValue
            result.put(this.keyHandler.getColumnName(), keyValue);
        }
        return result;
    }

    @Override
    public Map<String, String> inquireByKey(String keyValue) {
        Map<String, String> result = null;
        //开启连接
        Jedis jedis = this.jedisPool.getResource();
        try {
            result = this.localInquireByKey(keyValue, jedis);
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
        //开启连接
        Jedis jedis = this.jedisPool.getResource();
        try {
            //查询
            for (String keyValue : keyValueList) {
                result = this.localInquireByKey(keyValue, jedis);
                if (result != null) {
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
        StringBuilder strBuilder = new StringBuilder(32);
        String columnValue;
        String columnName;
        String columnIndexKey;
        String score;
        long defaultScore = System.currentTimeMillis();
        Map<String, String> insertMap = new HashMap<String, String>(entityMap.size(), 1);
        //开启连接
        Jedis jedis = this.jedisPool.getResource();
        try {
            //过滤多余的列，并更新index
            jedis.select(0);
            for (RColumnHandler rColumnHandler : this.columnHandlerList) {
                columnName = rColumnHandler.getColumnName();
                columnValue = entityMap.get(columnName);
                if (columnValue != null) {
                    insertMap.put(columnName, columnValue);
                    if (rColumnHandler.getColumnType() == ColumnTypeEnum.INDEX) {
                        //如果该列是索引类型，则额外更新列信息
                        strBuilder.append(this.columnIndexKeyPrefix).append(columnName)
                                .append(this.connector).append(columnValue);
                        columnIndexKey = strBuilder.toString();
                        strBuilder.setLength(0);
                        score = entityMap.get(columnIndexKey);
                        if (score == null) {
                            jedis.zadd(columnIndexKey, defaultScore, keyValue);
                        } else {
                            jedis.zadd(columnIndexKey, Long.parseLong(score), keyValue);
                        }
                    }
                }
            }
            //保存key索引
            score = entityMap.get(this.tableIndexKey);
            if (score == null) {
                jedis.zadd(this.tableIndexKey, defaultScore, keyValue);
            } else {
                jedis.zadd(this.tableIndexKey, Long.parseLong(score), keyValue);
            }
            if (insertMap.isEmpty() == false) {
                //插入到对应的db
                jedis.select(this.dbIndex);
                jedis.hmset(keyValue, insertMap);
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
        String columnIndexKey;
        String score;
        long defaultScore;
        StringBuilder strBuilder = new StringBuilder(32);
        Map<String, String> insertMap = new HashMap<String, String>(8, 1);
        //开启连接
        Jedis jedis = this.jedisPool.getResource();
        try {
            for (Map<String, String> entityMap : entityMapList) {
                keyValue = entityMap.get(keyName);
                if (keyValue == null) {
                    throw new RuntimeException("Can not find keyValue when insert:" + entityMap.toString());
                }
                defaultScore = System.currentTimeMillis();
                insertMap.clear();
                //过滤多余的列，并更新index
                jedis.select(0);
                for (RColumnHandler rColumnHandler : this.columnHandlerList) {
                    columnName = rColumnHandler.getColumnName();
                    columnValue = entityMap.get(columnName);
                    if (columnValue != null) {
                        insertMap.put(columnName, columnValue);
                        if (rColumnHandler.getColumnType() == ColumnTypeEnum.INDEX) {
                            //如果该列是索引类型，则额外更新列信息
                            strBuilder.append(this.columnIndexKeyPrefix).append(columnName)
                                    .append(this.connector).append(columnValue);
                            columnIndexKey = strBuilder.toString();
                            strBuilder.setLength(0);
                            score = entityMap.get(columnIndexKey);
                            if (score == null) {
                                jedis.zadd(columnIndexKey, defaultScore, keyValue);
                            } else {
                                jedis.zadd(columnIndexKey, Long.parseLong(score), keyValue);
                            }
                        }
                    }
                }
                //保存key索引
                score = entityMap.get(this.tableIndexKey);
                if (score == null) {
                    jedis.zadd(this.tableIndexKey, defaultScore, keyValue);
                } else {
                    jedis.zadd(this.tableIndexKey, Long.parseLong(score), keyValue);
                }
                if (insertMap.isEmpty() == false) {
                    //插入到对应的db
                    jedis.select(this.dbIndex);
                    jedis.hmset(keyValue, insertMap);
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
        StringBuilder strBuilder = new StringBuilder(32);
        //查询旧记录
        Map<String, String> oldEntityMap = this.inquireByKey(keyValue);
        if (oldEntityMap != null) {
            //比对变化，更新数据及索引
            String columnValue;
            String oldColumnValue;
            String columnName;
            String columnIndexKey;
            String score;
            long defaultScore = System.currentTimeMillis();
            Map<String, String> updateMap = new HashMap<String, String>(4, 1);
            //开启连接
            Jedis jedis = this.jedisPool.getResource();
            try {
                jedis.select(0);
                for (RColumnHandler rColumnHandler : this.columnHandlerList) {
                    columnName = rColumnHandler.getColumnName();
                    columnValue = entityMap.get(columnName);
                    if (columnValue != null) {
                        oldColumnValue = oldEntityMap.get(columnName);
                        if (oldColumnValue == null || oldColumnValue.equals(columnValue) == false) {
                            //列值发生变化
                            updateMap.put(columnName, columnValue);
                        }
                        if (rColumnHandler.getColumnType() == ColumnTypeEnum.INDEX) {
                            //是索引列
                            if (oldColumnValue != null && oldColumnValue.equals(columnValue) == false) {
                                //删除旧索引
                                strBuilder.append(this.columnIndexKeyPrefix).append(columnName)
                                        .append(this.connector).append(oldColumnValue);
                                columnIndexKey = strBuilder.toString();
                                strBuilder.setLength(0);
                                jedis.zrem(columnIndexKey, keyValue);
                            }
                            //更新新索引
                            strBuilder.append(this.columnIndexKeyPrefix).append(columnName)
                                    .append(this.connector).append(columnValue);
                            columnIndexKey = strBuilder.toString();
                            strBuilder.setLength(0);
                            score = entityMap.get(columnIndexKey);
                            if (score != null) {
                                jedis.zadd(columnIndexKey, Long.parseLong(score), keyValue);
                            } else {
                                if (oldColumnValue == null || oldColumnValue.equals(columnValue) == false) {
                                    jedis.zadd(columnIndexKey, defaultScore, keyValue);
                                }
                            }
                        }
                    }
                }
                //保存key索引
                score = entityMap.get(this.tableIndexKey);
                if (score != null) {
                    jedis.zadd(this.tableIndexKey, Long.parseLong(score), keyValue);
                }
                if (updateMap.isEmpty() == false) {
                    //更新到对应的db
                    jedis.select(this.dbIndex);
                    jedis.hmset(keyValue, updateMap);
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
        String columnIndexKey;
        String score;
        long defaultScore;
        Map<String, String> updateMap = new HashMap<String, String>(4, 1);
        //开启连接
        Jedis jedis = this.jedisPool.getResource();
        try {
            for (Map<String, String> entityMap : entityMapList) {
                keyValue = entityMap.get(keyName);
                if (keyValue != null) {
                    //keyValue 存在,查询旧记录
                    oldEntityMap = this.localInquireByKey(keyValue, jedis);
                    if (oldEntityMap != null) {
                        jedis.select(0);
                        defaultScore = System.currentTimeMillis();
                        updateMap.clear();
                        //比对变化，更新数据及索引
                        for (RColumnHandler rColumnHandler : this.columnHandlerList) {
                            columnName = rColumnHandler.getColumnName();
                            columnValue = entityMap.get(columnName);
                            if (columnValue != null) {
                                oldColumnValue = oldEntityMap.get(columnName);
                                if (oldColumnValue == null || oldColumnValue.equals(columnValue) == false) {
                                    //值变化
                                    updateMap.put(columnName, columnValue);
                                }
                                if (rColumnHandler.getColumnType() == ColumnTypeEnum.INDEX) {
                                    //是索引列
                                    if (oldColumnValue != null && oldColumnValue.equals(columnValue) == false) {
                                        //删除旧索引
                                        strBuilder.append(this.columnIndexKeyPrefix).append(columnName)
                                                .append(this.connector).append(oldColumnValue);
                                        columnIndexKey = strBuilder.toString();
                                        strBuilder.setLength(0);
                                        jedis.zrem(columnIndexKey, keyValue);
                                    }
                                    //更新新索引
                                    strBuilder.append(this.columnIndexKeyPrefix).append(columnName)
                                            .append(this.connector).append(columnValue);
                                    columnIndexKey = strBuilder.toString();
                                    strBuilder.setLength(0);
                                    score = entityMap.get(columnIndexKey);
                                    if (score != null) {
                                        jedis.zadd(columnIndexKey, Long.parseLong(score), keyValue);
                                    } else {
                                        if (oldColumnValue == null || oldColumnValue.equals(columnValue) == false) {
                                            jedis.zadd(columnIndexKey, defaultScore, keyValue);
                                        }
                                    }
                                }
                            }
                        }
                        //保存key索引
                        score = entityMap.get(this.tableIndexKey);
                        if (score != null) {
                            jedis.zadd(this.tableIndexKey, Long.parseLong(score), keyValue);
                        }
                        if (updateMap.isEmpty() == false) {
                            //更新到对应的db
                            jedis.select(this.dbIndex);
                            jedis.hmset(keyValue, updateMap);
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
        //开启连接
        Jedis jedis = this.jedisPool.getResource();
        try {
            //查询
            Map<String, String> entityMap = this.localInquireByKey(keyValue, jedis);
            if (entityMap != null) {
                //删除相关索引
                String columnValue;
                String columnName;
                String columnIndexKey;
                StringBuilder strBuilder = new StringBuilder(32);
                jedis.select(0);
                for (RColumnHandler rColumnHandler : this.columnHandlerList) {
                    columnName = rColumnHandler.getColumnName();
                    columnValue = entityMap.get(columnName);
                    if (columnValue != null && rColumnHandler.getColumnType() == ColumnTypeEnum.INDEX) {
                        //如果该列是索引类型，则额外更新列信息
                        strBuilder.append(this.columnIndexKeyPrefix).append(columnName)
                                .append(this.connector).append(columnValue);
                        columnIndexKey = strBuilder.toString();
                        strBuilder.setLength(0);
                        jedis.zrem(columnIndexKey, keyValue);
                    }
                }
                //删除主键索引
                jedis.zrem(this.tableIndexKey, keyValue);
                //删除
                jedis.select(this.dbIndex);
                jedis.del(keyValue);
            }
        } finally {
            //关闭连接
            this.jedisPool.returnResource(jedis);
        }
    }

    @Override
    public void batchDelete(List<String> keyValueList) {
        StringBuilder strBuilder = new StringBuilder(32);
        String columnValue;
        String columnName;
        String columnIndexKey;
        Map<String, String> entityMap;
        //开启连接
        Jedis jedis = this.jedisPool.getResource();
        try {
            //删除
            for (String keyValue : keyValueList) {
                //查询
                entityMap = this.localInquireByKey(keyValue, jedis);
                if (entityMap != null) {
                    jedis.select(0);
                    //删除索引列
                    for (RColumnHandler rColumnHandler : this.columnHandlerList) {
                        columnName = rColumnHandler.getColumnName();
                        columnValue = entityMap.get(columnName);
                        if (columnValue != null && rColumnHandler.getColumnType() == ColumnTypeEnum.INDEX) {
                            //如果该列是索引类型，则额外更新列信息
                            strBuilder.append(this.columnIndexKeyPrefix).append(columnName)
                                    .append(this.connector).append(columnValue);
                            columnIndexKey = strBuilder.toString();
                            strBuilder.setLength(0);
                            jedis.zrem(columnIndexKey, keyValue);
                        }
                    }
                    //删除主键索引
                    jedis.zrem(this.tableIndexKey, keyValue);
                    //删除
                    jedis.select(this.dbIndex);
                    jedis.del(keyValue);
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
        long pageIndex = inquirePageContext.getPageIndex();
        long pageSize = inquirePageContext.getPageSize();
        long start = (pageIndex - 1) * pageSize;
        long end = pageIndex * pageSize - 1;
        //开启连接
        Jedis jedis = this.jedisPool.getResource();
        try {
            jedis.select(0);
            Set<String> keySet = jedis.zrange(this.tableIndexKey, start, end);
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
        long pageIndex = inquirePageContext.getPageIndex();
        long pageSize = inquirePageContext.getPageSize();
        long start = (pageIndex - 1) * pageSize;
        long end = pageIndex * pageSize - 1;
        //开启连接
        Jedis jedis = this.jedisPool.getResource();
        try {
            jedis.select(0);
            Set<String> keySet = jedis.zrevrange(this.tableIndexKey, start, end);
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
        //开启连接
        Jedis jedis = this.jedisPool.getResource();
        try {
            jedis.select(0);
            result = jedis.zcard(this.tableIndexKey);
        } finally {
            //关闭连接
            this.jedisPool.returnResource(jedis);
        }
        return result;
    }

    @Override
    public List<String> inquireKeysByIndex(InquireIndexPageContext inquireIndexPageContext) {
        String indexName = inquireIndexPageContext.getIndexName();
        //判断是否存在该索引
        List<String> resultList;
        if (this.indexColumnNameSet.contains(indexName)) {
            //构造redis 列索引key
            StringBuilder strBuilder = new StringBuilder(32);
            strBuilder.append(this.columnIndexKeyPrefix).append(indexName)
                    .append(this.connector).append(inquireIndexPageContext.getIndexValue());
            String columnIndexKey = strBuilder.toString();
            long pageIndex = inquireIndexPageContext.getPageIndex();
            long pageSize = inquireIndexPageContext.getPageSize();
            long start = (pageIndex - 1) * pageSize;
            long end = pageIndex * pageSize - 1;
            //开启连接
            Jedis jedis = this.jedisPool.getResource();
            try {
                jedis.select(0);
                Set<String> keySet = jedis.zrange(columnIndexKey, start, end);
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
    public List<String> inquireKeysByIndexDESC(InquireIndexPageContext inquireIndexPageContext) {
        String indexName = inquireIndexPageContext.getIndexName();
        //判断是否存在该索引
        List<String> resultList;
        if (this.indexColumnNameSet.contains(indexName)) {
            //构造redis 列索引key
            StringBuilder strBuilder = new StringBuilder(32);
            strBuilder.append(this.columnIndexKeyPrefix).append(indexName)
                    .append(this.connector).append(inquireIndexPageContext.getIndexValue());
            String columnIndexKey = strBuilder.toString();
            long pageIndex = inquireIndexPageContext.getPageIndex();
            long pageSize = inquireIndexPageContext.getPageSize();
            long start = (pageIndex - 1) * pageSize;
            long end = pageIndex * pageSize - 1;
            //开启连接
            Jedis jedis = this.jedisPool.getResource();
            try {
                jedis.select(0);
                Set<String> keySet = jedis.zrevrange(columnIndexKey, start, end);
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
            strBuilder.append(this.columnIndexKeyPrefix).append(indexName)
                    .append(this.connector).append(indexValue);
            String columnIndexKey = strBuilder.toString();
            //开启连接
            Jedis jedis = this.jedisPool.getResource();
            try {
                jedis.select(0);
                result = jedis.zcard(columnIndexKey);
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
        //开启连接
        Jedis jedis = this.jedisPool.getResource();
        try {
            jedis.select(this.dbIndex);
            result = jedis.hincrBy(keyValue, columnName, value);
        } finally {
            //关闭连接
            this.jedisPool.returnResource(jedis);
        }
        return result;
    }

    @Override
    public void updateKeySorce(String keyValue, long score) {
        //开启连接
        Jedis jedis = this.jedisPool.getResource();
        try {
            //保存key索引
            jedis.select(0);
            jedis.zadd(this.tableIndexKey, score, keyValue);
        } finally {
            //关闭连接
            this.jedisPool.returnResource(jedis);
        }
    }

    @Override
    public void updateIndexKeySorce(String keyValue, String columnName, String columnValue, long score) {
        String columnIndexKey = this.getColumnIndexKey(columnName, columnValue);
        //开启连接
        Jedis jedis = this.jedisPool.getResource();
        try {
            //保存key索引
            jedis.select(0);
            jedis.zadd(columnIndexKey, score, keyValue);
        } finally {
            //关闭连接
            this.jedisPool.returnResource(jedis);
        }
    }

    private String createSortedSetKey(String keyValue, String sortedSetName) {
        StringBuilder stringBuilder = new StringBuilder(32);
        stringBuilder.append(this.sortedSetPrefix).append(sortedSetName)
                .append(this.connector).append(keyValue);
        return stringBuilder.toString();
    }

    @Override
    public void sortedSetAdd(String keyValue, String sortedSetName, String value, long score) {
        if (this.sortedSetNames.contains(sortedSetName)) {
            String key = this.createSortedSetKey(keyValue, sortedSetName);
            //开启连接
            Jedis jedis = this.jedisPool.getResource();
            try {
                //保存key索引
                jedis.select(this.dbIndex);
                jedis.zadd(key, score, value);
            } finally {
                //关闭连接
                this.jedisPool.returnResource(jedis);
            }
        }
    }

    @Override
    public void sortedSetRemove(String keyValue, String sortedSetName, String value) {
        if (this.sortedSetNames.contains(sortedSetName)) {
            String key = this.createSortedSetKey(keyValue, sortedSetName);
            //开启连接
            Jedis jedis = this.jedisPool.getResource();
            try {
                //保存key索引
                jedis.select(this.dbIndex);
                jedis.zrem(key, value);
            } finally {
                //关闭连接
                this.jedisPool.returnResource(jedis);
            }
        }
    }

    @Override
    public List<String> sortedSet(String keyValue, String sortedSetName) {
        List<String> resultList;
        if (this.sortedSetNames.contains(sortedSetName)) {
            String key = this.createSortedSetKey(keyValue, sortedSetName);
            //开启连接
            Jedis jedis = this.jedisPool.getResource();
            try {
                //保存key索引
                jedis.select(this.dbIndex);
                Set<String> keySet = jedis.zrange(key, 0, 199);
                resultList = new ArrayList<String>(keySet.size());
                resultList.addAll(keySet);
            } finally {
                //关闭连接
                this.jedisPool.returnResource(jedis);
            }
        } else {
            resultList = Collections.EMPTY_LIST;
        }
        return resultList;
    }

    @Override
    public List<String> sortedSetDESC(String keyValue, String sortedSetName) {
        List<String> resultList;
        if (this.sortedSetNames.contains(sortedSetName)) {
            String key = this.createSortedSetKey(keyValue, sortedSetName);
            //开启连接
            Jedis jedis = this.jedisPool.getResource();
            try {
                //保存key索引
                jedis.select(this.dbIndex);
                Set<String> keySet = jedis.zrevrange(key, 0, 199);
                resultList = new ArrayList<String>(keySet.size());
                resultList.addAll(keySet);
            } finally {
                //关闭连接
                this.jedisPool.returnResource(jedis);
            }
        } else {
            resultList = Collections.EMPTY_LIST;
        }
        return resultList;
    }

    @Override
    public void sortedSetClear(String keyValue, String sortedSetName) {
        if (this.sortedSetNames.contains(sortedSetName)) {
            String key = this.sortedSetPrefix + sortedSetName + this.connector + keyValue;
            //开启连接
            Jedis jedis = this.jedisPool.getResource();
            try {
                //保存key索引
                jedis.select(this.dbIndex);
                jedis.del(key);
            } finally {
                //关闭连接
                this.jedisPool.returnResource(jedis);
            }
        }
    }

    @Override
    public void truncate() {
        //开启连接
        Jedis jedis = this.jedisPool.getResource();
        try {
            jedis.select(0);
            //删除主键索引
            jedis.del(this.tableIndexKey);
            //删除外键索引
            Set<String> indexKeys = jedis.keys(this.columnIndexKeyPrefix + "*");
            for (String indexKey : indexKeys) {
                jedis.del(indexKey);
            }
            //删除表数据
            jedis.select(this.dbIndex);
            jedis.flushDB();
        } finally {
            //关闭连接
            this.jedisPool.returnResource(jedis);
        }
    }
}
