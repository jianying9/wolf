package com.wolf.framework.dao.parser;

import com.wolf.framework.config.FrameworkLoggerEnum;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.dao.HEntityDao;
import com.wolf.framework.dao.HEntityDaoBuilder;
import com.wolf.framework.dao.HEntityDaoContext;
import com.wolf.framework.dao.annotation.DaoConfig;
import com.wolf.framework.dao.annotation.HColumnConfig;
import com.wolf.framework.dao.annotation.HDaoConfig;
import com.wolf.framework.logger.LogFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

/**
 * 负责解析 HDaoConfig
 *
 * @author aladdin
 */
public class HDaoConfigParser<T extends Entity> {

    private final Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.FRAMEWORK);
    private final HEntityDaoContext<T> entityDaoContext;

    public HDaoConfigParser(HEntityDaoContext<T> entityDaoContext) {
        this.entityDaoContext = entityDaoContext;
    }

    /**
     * 解析方法
     *
     * @param <T>
     * @param clazz
     */
    public void parse(final Class<T> clazz) {
        this.logger.debug("--parsing H entity DAO {}--", clazz.getName());
        if (clazz.isAnnotationPresent(HDaoConfig.class)) {
            //1.获取注解DaoConfig
            final HDaoConfig daoConfig = clazz.getAnnotation(HDaoConfig.class);
            //3.获取实体标识
            final String tableName = daoConfig.tableName();
            //5获取该实体所有字段集合
            Field[] fieldTemp = clazz.getDeclaredFields();
            //ColumnHandler
            HColumnHandler keyHandler = null;
            //column
            List<HColumnHandler> columnHandlerList = new ArrayList<HColumnHandler>(fieldTemp.length);
            HColumnHandler columnHandler;
            int modifier;
            String fieldName;
            HColumnConfig columnConfig;
            for (Field field : fieldTemp) {
                modifier = field.getModifiers();
                if (!Modifier.isStatic(modifier)) {
                    //非静态字段
                    fieldName = field.getName();
                    if (field.isAnnotationPresent(HColumnConfig.class)) {
                        //
                        columnConfig = field.getAnnotation(HColumnConfig.class);
                        if (columnConfig.key()) {
                            if (keyHandler == null) {
                                keyHandler = new HColumnHandlerImpl(fieldName, columnConfig.desc());
                            } else {
                                throw new RuntimeException("There was an error building H entityDao:" + clazz.getName() + ". Cause:too many key");
                            }
                        } else {
                            columnHandler = new HColumnHandlerImpl(fieldName, columnConfig.desc());
                            columnHandlerList.add(columnHandler);
                        }
                    }
                }
            }
            if(keyHandler == null) {
                throw new RuntimeException("There was an error building H entityDao:" + clazz.getName() + ". Cause:can not find key");
            }
            HEntityDaoBuilder<T> entityDaoBuilder = new HEntityDaoBuilder<T>(
                    tableName,
                    keyHandler,
                    columnHandlerList,
                    clazz,
                    this.entityDaoContext);
            HEntityDao<T> entityDao = entityDaoBuilder.build();
            entityDaoContext.putHEntityDao(clazz, entityDao, tableName);
            this.logger.debug("--parse H entity DAO {} finished--", clazz.getName());
        } else {
            this.logger.error("--parse H entity DAO {} missing annotation HDaoConfig--", clazz.getName());
        }
    }
}
