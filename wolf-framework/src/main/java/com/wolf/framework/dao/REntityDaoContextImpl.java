package com.wolf.framework.dao;

import com.wolf.framework.config.FrameworkConfig;
import com.wolf.framework.context.ApplicationContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisPool;

/**
 *
 * @author aladdin
 */
public class REntityDaoContextImpl<T extends Entity> implements REntityDaoContext<T> {

    private final Map<String, String> existClassMap = new HashMap<String, String>(128);
    //entity处理类集合
    private final Map<Class<T>, REntityDao<T>> entityDaoMap;
    //
    private final ApplicationContext applicationContext;
    private final JedisPool jedisPool;

    public REntityDaoContextImpl(ApplicationContext applicationContext) {
        this.entityDaoMap = new HashMap<Class<T>, REntityDao<T>>(8, 1);
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
        poolConfig.setMaxIdle(maxPoolSize);
        poolConfig.setMinIdle(minPoolSize);
        this.jedisPool = new JedisPool(poolConfig, host, port);
    }

    @Override
    public void putREntityDao(Class clazz, REntityDao entityDao, String entityName) {
        //判断实体是否存在
        if (this.entityDaoMap.containsKey(clazz)) {
            String existClassName = this.existClassMap.get(entityName);
            if (existClassName == null) {
                existClassName = "NULL";
            }
            StringBuilder errBuilder = new StringBuilder(1024);
            errBuilder.append("Error putting REntityDao. Cause: entityName reduplicated : ")
                    .append(entityName).append("\n").append("exist class : ").append(existClassName).append("\n")
                    .append("this class : ").append(clazz.getName());
            throw new RuntimeException(errBuilder.toString());
        }
        this.entityDaoMap.put(clazz, entityDao);
        this.existClassMap.put(entityName, clazz.getName());
    }

    @Override
    public REntityDao getREntityDao(Class<T> clazz) {
        return this.entityDaoMap.get(clazz);
    }

    @Override
    public Map<Class<T>, REntityDao<T>> getREntityDaoMap() {
        return Collections.unmodifiableMap(this.entityDaoMap);
    }

    @Override
    public boolean assertExistREntity(Class<T> clazz) {
        return this.entityDaoMap.containsKey(clazz);
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    @Override
    public JedisPool getJedisPool() {
        return this.jedisPool;
    }
}
