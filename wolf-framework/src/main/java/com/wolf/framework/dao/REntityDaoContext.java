package com.wolf.framework.dao;

import com.wolf.framework.context.ApplicationContext;
import java.util.Map;
import redis.clients.jedis.JedisPool;

/**
 *
 * @author aladdin
 */
public interface REntityDaoContext<T extends Entity> {

    public void putREntityDao(final Class<T> clazz, final REntityDao<T> entityDao, final String entityName, int dbIndex);

    public REntityDao getREntityDao(final Class<T> clazz);

    public Map<Class<T>, REntityDao<T>> getREntityDaoMap();

    public boolean assertExistREntity(final Class<T> clazz);

    public ApplicationContext getApplicationContext();
    
    public JedisPool getJedisPool();
}
