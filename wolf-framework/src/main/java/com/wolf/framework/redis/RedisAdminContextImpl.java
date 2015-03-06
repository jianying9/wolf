package com.wolf.framework.redis;

import com.wolf.framework.config.FrameworkConfig;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.dao.Entity;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisPool;

/**
 *
 * @author jianying9
 */
public class RedisAdminContextImpl implements RedisAdminContext {
    //

    private final Map<Class, RedisHandler> redisHandlerMap = new HashMap<Class, RedisHandler>(8, 1);
    private final ApplicationContext applicationContext;
    private final JedisPool jedisPool;

    public RedisAdminContextImpl(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        String host = this.applicationContext.getParameter(FrameworkConfig.REDIS_SERVER_HOST);
        if (host == null) {
            throw new RuntimeException("Error when init redis pool. Cause:can not find " + FrameworkConfig.REDIS_SERVER_HOST);
        }
        String portStr = this.applicationContext.getParameter(FrameworkConfig.REDIS_SERVER_PORT);
        if (portStr == null) {
            throw new RuntimeException("Error when init redis pool. Cause:can not find " + FrameworkConfig.REDIS_SERVER_PORT);
        }
        int port = Integer.parseInt(portStr);
        int maxPoolSize;
        String maxPoolSizeStr = this.applicationContext.getParameter(FrameworkConfig.REDIS_MAX_POOL_SIZE);
        if (maxPoolSizeStr == null) {
            maxPoolSize = 100;
        } else {
            maxPoolSize = Integer.parseInt(maxPoolSizeStr);
            if (maxPoolSize > 100) {
                maxPoolSize = 100;
            }
        }
        int minPoolSize;
        String minPoolSizeStr = this.applicationContext.getParameter(FrameworkConfig.REDIS_MIN_POOL_SIZE);
        if (minPoolSizeStr == null) {
            minPoolSize = 10;
        } else {
            minPoolSize = Integer.parseInt(minPoolSizeStr);
            if (minPoolSize < 0) {
                minPoolSize = 10;
            }
            if (minPoolSize > maxPoolSize) {
                minPoolSize = maxPoolSize;
            }
        }
        //
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(maxPoolSize);
        poolConfig.setMaxIdle(maxPoolSize);
        poolConfig.setMinIdle(minPoolSize);
        this.jedisPool = new JedisPool(poolConfig, host, port);
    }

    @Override
    public JedisPool getJedisPool() {
        return this.jedisPool;
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    @Override
    public <T extends Entity> void putRedisHandler(Class<T> clazz, RedisHandler redisHandler, String entityName) {
        if (this.redisHandlerMap.containsKey(clazz)) {
            StringBuilder errBuilder = new StringBuilder(1024);
            errBuilder.append("Error putting RedisHandler. Cause: entityName duplicated : ")
                    .append(entityName).append("\n");
            throw new RuntimeException(errBuilder.toString());
        }
        this.redisHandlerMap.put(clazz, redisHandler);
    }

    @Override
    public <T extends Entity> RedisHandler getRedisHandler(Class<T> clazz) {
        return this.redisHandlerMap.get(clazz);
    }
}
