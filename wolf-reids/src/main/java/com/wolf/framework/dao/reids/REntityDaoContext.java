package com.wolf.framework.dao.reids;

import com.wolf.framework.dao.Entity;
import com.wolf.framework.dao.reids.RedisAdminContext;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public interface REntityDaoContext<T extends Entity> {

    public void putREntityDao(final Class<T> clazz, final REntityDao<T> entityDao, final String entityName);

    public REntityDao getREntityDao(final Class<T> clazz);

    public Map<Class<T>, REntityDao<T>> getREntityDaoMap();

    public boolean assertExistREntity(final Class<T> clazz);
    
    public RedisAdminContext getRedisAdminContext();
}