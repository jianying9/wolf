package com.wolf.framework.dao.reids;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.dao.Entity;
import redis.clients.jedis.JedisPool;

/**
 *
 * @author jianying9
 */
public interface RedisAdminContext {
    
    public JedisPool getJedisPool();
    
    public ApplicationContext getApplicationContext();
    
    public <T extends Entity> void putRedisHandler(final Class<T> clazz, final RedisHandler redisHandler, final String entityName);
    
    public <T extends Entity> RedisHandler getRedisHandler(final Class<T> clazz);
}
