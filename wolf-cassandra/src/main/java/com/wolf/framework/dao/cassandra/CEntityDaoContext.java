package com.wolf.framework.dao.cassandra;

import com.wolf.framework.dao.Entity;
import java.util.Map;

/**
 *
 * @author jianying9
 * @param <T>
 */
public interface CEntityDaoContext<T extends Entity> {

    public void putCEntityDao(final Class<T> clazz, final CEntityDao<T> entityDao, final String entityName);

    public CEntityDao getCEntityDao(final Class<T> clazz);

    public Map<Class<T>, CEntityDao<T>> getCEntityDaoMap();

    public boolean assertExistCEntity(final Class<T> clazz);
}
