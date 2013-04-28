package com.wolf.framework.dao.parser;

import com.wolf.framework.config.FrameworkLoggerEnum;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.dao.EntityDao;
import com.wolf.framework.dao.EntityDaoBuilder;
import com.wolf.framework.dao.EntityDaoContext;
import com.wolf.framework.dao.annotation.ColumnConfig;
import com.wolf.framework.dao.annotation.ColumnTypeEnum;
import com.wolf.framework.dao.annotation.DaoConfig;
import com.wolf.framework.data.DataHandler;
import com.wolf.framework.data.DataHandlerFactory;
import com.wolf.framework.logger.LogFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

/**
 * 负责解析 DaoConfig
 *
 * @author aladdin
 */
public class DaoConfigParser<T extends Entity> {

    private final Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.FRAMEWORK);
    private final EntityDaoContext<T> entityDaoContext;

    public DaoConfigParser(EntityDaoContext<T> entityDaoContext) {
        this.entityDaoContext = entityDaoContext;
    }

    /**
     * 解析方法
     *
     * @param <T>
     * @param clazz
     */
    public void parse(final Class<T> clazz) {
        this.logger.debug("--parsing entity DAO {}--", clazz.getName());
        if (clazz.isAnnotationPresent(DaoConfig.class)) {
            final DataHandlerFactory dataHandlerFactory = this.entityDaoContext.getDataHandlerFactory();
            //1.获取注解DaoConfig
            final DaoConfig daoConfig = clazz.getAnnotation(DaoConfig.class);
            //3.获取实体标识
            final String tableName = daoConfig.tableName();
            //5获取该实体所有字段集合
            Field[] fieldTemp = clazz.getDeclaredFields();
            //ColumnHandler
            ColumnHandler keyHandler = null;
            //column
            List<ColumnHandler> columnHandlerList = new ArrayList<ColumnHandler>(fieldTemp.length);
            ColumnHandler columnHandler;
            int modifier;
            String fieldName;
            ColumnConfig columnConfig;
            DataHandler dataHandler;
            ColumnTypeEnum columnTypeEnum;
            for (Field field : fieldTemp) {
                modifier = field.getModifiers();
                if (!Modifier.isStatic(modifier)) {
                    //非静态字段
                    fieldName = field.getName();
                    if (field.isAnnotationPresent(ColumnConfig.class)) {
                        //
                        columnConfig = field.getAnnotation(ColumnConfig.class);
                        dataHandler = dataHandlerFactory.getDataHandler(columnConfig.dataTypeEnum());
                        columnTypeEnum = columnConfig.columnTypeEnum();
                        if (columnTypeEnum == ColumnTypeEnum.KEY) {
                            if (keyHandler == null) {
                                keyHandler = new ColumnHandlerImpl(fieldName, columnTypeEnum, columnConfig.desc(), dataHandler);
                            } else {
                                throw new RuntimeException("There was an error building entityDao:" + clazz.getName() + ". Cause:too many key");
                            }
                        } else {
                            columnHandler = new ColumnHandlerImpl(fieldName, columnTypeEnum, columnConfig.desc(), dataHandler);
                            columnHandlerList.add(columnHandler);
                        }
                    }
                }
            }
            if(keyHandler == null) {
                throw new RuntimeException("There was an error building entityDao:" + clazz.getName() + ". Cause:can not find key");
            }
            final boolean useCache = daoConfig.useCache();
            final int maxEntriesLocalHeap = daoConfig.maxEntriesLocalHeap();
            final int timeToIdleSeconds = daoConfig.timeToIdleSeconds();
            final int timeToLiveSeconds = daoConfig.timeToLiveSeconds();
            EntityDaoBuilder<T> entityDaoBuilder = new EntityDaoBuilder<T>(
                    tableName,
                    keyHandler,
                    columnHandlerList,
                    clazz,
                    useCache,
                    maxEntriesLocalHeap,
                    timeToIdleSeconds,
                    timeToLiveSeconds,
                    this.entityDaoContext);
            EntityDao<T> entityDao = entityDaoBuilder.build();
            entityDaoContext.putEntityDao(clazz, entityDao, tableName);
            this.logger.debug("--parse entity DAO {} finished--", clazz.getName());
        } else {
            this.logger.error("--parse entity DAO {} missing annotation DaoConfig--", clazz.getName());
        }
    }
}
