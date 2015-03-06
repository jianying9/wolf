package com.wolf.framework.dao.reids;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.dao.Entity;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public final class TestRedisHandler {
    
    static RedisAdminContext redisAdminContext;

    /**
     * 清空某张redis表
     *
     * @param <T>
     * @param clazz
     */
    public <T extends Entity> void truncateRedis(Class<T> clazz) {
        RedisHandler redisHandler = redisAdminContext.getRedisHandler(clazz);
        if (redisHandler != null) {
            redisHandler.truncate();
        }
    }

    /**
     * 往指定的redis表插入某行记录
     *
     * @param <T>
     * @param clazz
     * @param entityMap
     */
    public <T extends Entity> void insertRedis(Class<T> clazz, Map<String, String> entityMap) {
        RedisHandler redisHandler = redisAdminContext.getRedisHandler(clazz);
        if (redisHandler != null) {
            redisHandler.insert(entityMap);
        }
    }

    /**
     * 在指定的redis表中删除某行记录
     *
     * @param <T>
     * @param clazz
     * @param keyValue
     */
    public <T extends Entity> void deleteRedis(Class<T> clazz, String keyValue) {
        RedisHandler redisHandler = redisAdminContext.getRedisHandler(clazz);
        if (redisHandler != null) {
            redisHandler.delete(keyValue);
        }
    }

    /**
     * 指定某个sorted set的类型的扩展列,增加一个键值
     *
     * @param <T>
     * @param clazz
     * @param keyValue
     * @param sortedSetName
     * @param value
     * @param score
     */
    public <T extends Entity> void sortedSetAdd(Class<T> clazz, String keyValue, String sortedSetName, String value, long score) {
        RedisHandler redisHandler = redisAdminContext.getRedisHandler(clazz);
        if (redisHandler != null) {
            redisHandler.sortedSetAdd(keyValue, sortedSetName, value, score);
        }
    }

    /**
     * 指定某个sorted set的类型的扩展列,删除一个键值
     *
     * @param <T>
     * @param clazz
     * @param keyValue
     * @param sortedSetName
     * @param value
     */
    public <T extends Entity> void sortedSetRemove(Class<T> clazz, String keyValue, String sortedSetName, String value) {
        RedisHandler redisHandler = redisAdminContext.getRedisHandler(clazz);
        if (redisHandler != null) {
            redisHandler.sortedSetRemove(keyValue, sortedSetName, value);
        }
    }
}
