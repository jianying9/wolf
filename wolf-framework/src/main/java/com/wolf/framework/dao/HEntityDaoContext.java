package com.wolf.framework.dao;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.hbase.HTableHandler;
import com.wolf.framework.task.TaskExecutor;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public interface HEntityDaoContext<T extends Entity> {

    public void putHEntityDao(final Class<T> clazz, final HEntityDao<T> entityDao, final String entityName);

    public HEntityDao getHEntityDao(final Class<T> clazz);

    public Map<Class<T>, HEntityDao<T>> getHEntityDaoMap();

    public boolean assertExistHEntity(final Class<T> clazz);

    public HTableHandler getHTableHandler();

    public TaskExecutor getTaskExecutor();

    public ApplicationContext getApplicationContext();
}
