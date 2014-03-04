package com.wolf.framework.dao;

import com.wolf.framework.config.FrameworkConfig;
import com.wolf.framework.config.FrameworkLoggerEnum;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.context.Resource;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.data.DataHandlerFactory;
import com.wolf.framework.derby.DerbyResourceImpl;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.task.TaskExecutor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.Configuration;
import org.apache.derby.jdbc.ClientDataSource;
import org.apache.derby.jdbc.ClientDataSource40;
import org.apache.derby.jdbc.EmbeddedSimpleDataSource;
import org.slf4j.Logger;

/**
 * 全局信息构造类
 *
 * @author aladdin
 */
public class EntityDaoContextImpl<T extends Entity> implements EntityDaoContext<T> {

    protected final Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.FRAMEWORK);
    //缓存管理对象
    private final CacheManager cacheManager;
    private final Map<String, String> existClassMap = new HashMap<String, String>(128);
    private final TaskExecutor taskExecutor;
    private final DataSource dataSource;
    private final DataHandlerFactory dataHandlerFactory;
    //entity处理类集合
    private final Map<Class<T>, EntityDao<T>> entityDaoMap;
    //
    private final ApplicationContext applicationContext;

    @Override
    public final CacheManager getCacheManager() {
        return cacheManager;
    }

    @Override
    public final void putEntityDao(final Class<T> clazz, final EntityDao<T> entityDao, final String entityName) {
        if (this.entityDaoMap.containsKey(clazz)) {
            String existClassName = this.existClassMap.get(entityName);
            if (existClassName == null) {
                existClassName = "NULL";
            }
            StringBuilder errBuilder = new StringBuilder(1024);
            errBuilder.append("There was an error putting EntityDao. Cause: entityName reduplicated : ")
                    .append(entityName).append("\n").append("exist class : ").append(existClassName).append("\n")
                    .append("this class : ").append(clazz.getName());
            throw new RuntimeException(errBuilder.toString());
        }
        this.entityDaoMap.put(clazz, entityDao);
        this.existClassMap.put(entityName, clazz.getName());
    }

    @Override
    public Map<Class<T>, EntityDao<T>> getEntityDaoMap() {
        return Collections.unmodifiableMap(this.entityDaoMap);
    }

    /**
     * 构造函数
     *
     * @param properties
     */
    public EntityDaoContextImpl(ApplicationContext applicationContext, final TaskExecutor taskExecutor, final DataHandlerFactory dataHandlerFactory) {
        this.entityDaoMap = new HashMap<Class<T>, EntityDao<T>>(8, 1);
        this.dataHandlerFactory = dataHandlerFactory;
        this.applicationContext = applicationContext;
        this.taskExecutor = taskExecutor;
        //创建缓存管理对象
        final Configuration ehcacheConfig = new Configuration();
        ehcacheConfig.setName("EntityDao-cache");
        ehcacheConfig.setDynamicConfig(false);
        ehcacheConfig.setUpdateCheck(false);
        ehcacheConfig.setMonitoring("OFF");
        this.cacheManager = CacheManager.create(ehcacheConfig);
        cacheManager.removalAll();
        //创建数据源
        String type = this.applicationContext.getParameter(FrameworkConfig.DERBY_TYPE);
        if (type == null) {
            type = "";
        }
        if (type.equals(FrameworkConfig.JNDI)) {
            String jndiName = this.applicationContext.getParameter(FrameworkConfig.DERBY_JNDI_NAME);
            if (jndiName == null) {
                throw new RuntimeException("Error when init derby DataSource. Cause:can not find " + FrameworkConfig.DERBY_JNDI_NAME);
            }
            try {
                Context context = new InitialContext();
                this.dataSource = (DataSource) context.lookup(jndiName);
            } catch (NamingException ex) {
                this.logger.error("Error when lookup JNDI:" + jndiName, ex);
                throw new RuntimeException(ex);
            }
        } else {
            String databaseName = this.applicationContext.getParameter(FrameworkConfig.DERBY_DATABASE_NAME);
            if (databaseName == null) {
                throw new RuntimeException("Error when init derby DataSource. Cause:can not find " + FrameworkConfig.DERBY_DATABASE_NAME);
            }
            if (type.equals(FrameworkConfig.EMBEDDED)) {
                EmbeddedSimpleDataSource embeddedSimpleDataSource = new EmbeddedSimpleDataSource();
                embeddedSimpleDataSource.setDatabaseName(databaseName);
                embeddedSimpleDataSource.setCreateDatabase("create");
                Resource derbyResource = new DerbyResourceImpl(embeddedSimpleDataSource);
                this.applicationContext.addResource(derbyResource);
                this.dataSource = embeddedSimpleDataSource;
            } else if (type.equals(FrameworkConfig.REMOTE)) {
                String serverName = this.applicationContext.getParameter(FrameworkConfig.DERBY_SERVER_NAME);
                if (serverName == null) {
                    throw new RuntimeException("Error when init derby DataSource. Cause:can not find " + FrameworkConfig.DERBY_SERVER_NAME);
                }
                String serverPort = this.applicationContext.getParameter(FrameworkConfig.DERBY_SERVER_PORT);
                if (serverPort == null) {
                    throw new RuntimeException("Error when init derby DataSource. Cause:can not find " + FrameworkConfig.DERBY_SERVER_PORT);
                }
                ClientDataSource clientDataSource = new ClientDataSource40();
                clientDataSource.setCreateDatabase("create");
                clientDataSource.setDatabaseName(databaseName);
                clientDataSource.setServerName(serverName);
                clientDataSource.setPortNumber(Integer.parseInt(serverPort));
                this.dataSource = clientDataSource;
            } else {
                throw new RuntimeException("Error when init derby DataSource. Cause: invalid " + FrameworkConfig.DERBY_TYPE);
            }
        }
    }

    @Override
    public boolean assertExistEntity(final Class<T> clazz) {
        return this.entityDaoMap.containsKey(clazz);
    }

    @Override
    public EntityDao getEntityDao(final Class<T> clazz) {
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
    public DataSource getDataSource() {
        return this.dataSource;
    }

    @Override
    public DataHandlerFactory getDataHandlerFactory() {
        return this.dataHandlerFactory;
    }
}
