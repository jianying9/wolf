package com.wolf.framework.dao;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.hbase.HTableHandler;
import com.wolf.framework.hbase.HTableHandlerImpl;
import com.wolf.framework.task.TaskExecutor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;

/**
 * 全局信息构造类
 *
 * @author aladdin
 */
public class HEntityDaoContextImpl<T extends Entity> implements HEntityDaoContext<T> {

    //缓存管理对象
    private final HTableHandler hTableHandler;
    private final Map<String, String> existClassMap = new HashMap<String, String>(128);
    private final TaskExecutor taskExecutor;
    //entity处理类集合
    private final Map<Class<T>, HEntityDao<T>> entityDaoMap;
    //
    private final ApplicationContext applicationContext;

    /**
     * 构造函数
     *
     * @param properties
     */
    public HEntityDaoContextImpl(ApplicationContext applicationContext, final TaskExecutor taskExecutor) {
        this.entityDaoMap = new HashMap<Class<T>, HEntityDao<T>>(16, 1);
        this.applicationContext = applicationContext;
        this.taskExecutor = taskExecutor;
        Configuration config = HBaseConfiguration.create();
        this.hTableHandler = new HTableHandlerImpl(config);
    }

    @Override
    public final void putHEntityDao(final Class<T> clazz, final HEntityDao<T> entityDao, final String entityName) {
        if (this.entityDaoMap.containsKey(clazz)) {
            String existClassName = this.existClassMap.get(entityName);
            if (existClassName == null) {
                existClassName = "NULL";
            }
            StringBuilder errBuilder = new StringBuilder(1024);
            errBuilder.append("There was an error putting H entityDao. Cause: entityName reduplicated : ")
                    .append(entityName).append("\n").append("exist class : ").append(existClassName).append("\n")
                    .append("this class : ").append(clazz.getName());
            throw new RuntimeException(errBuilder.toString());
        }
        this.entityDaoMap.put(clazz, entityDao);
        this.existClassMap.put(entityName, clazz.getName());
    }

    @Override
    public Map<Class<T>, HEntityDao<T>> getHEntityDaoMap() {
        return Collections.unmodifiableMap(this.entityDaoMap);
    }

    @Override
    public boolean assertExistHEntity(final Class<T> clazz) {
        return this.entityDaoMap.containsKey(clazz);
    }

    @Override
    public HEntityDao getHEntityDao(final Class<T> clazz) {
        return this.entityDaoMap.get(clazz);
    }

    @Override
    public TaskExecutor getTaskExecutor() {
        return this.taskExecutor;
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    @Override
    public HTableHandler getHTableHandler() {
        return this.hTableHandler;
    }
}
