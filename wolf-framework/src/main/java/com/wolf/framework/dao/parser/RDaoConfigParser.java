package com.wolf.framework.dao.parser;

import com.wolf.framework.config.FrameworkLoggerEnum;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.dao.REntityDao;
import com.wolf.framework.dao.REntityDaoBuilder;
import com.wolf.framework.dao.REntityDaoContext;
import com.wolf.framework.dao.annotation.ColumnTypeEnum;
import com.wolf.framework.dao.annotation.RColumnConfig;
import com.wolf.framework.dao.annotation.RDaoConfig;
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
public class RDaoConfigParser<T extends Entity> {

    private final Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.FRAMEWORK);
    private final REntityDaoContext<T> entityDaoContext;

    public RDaoConfigParser(REntityDaoContext<T> entityDaoContext) {
        this.entityDaoContext = entityDaoContext;
    }

    /**
     * 解析方法
     *
     * @param <T>
     * @param clazz
     */
    public void parse(final Class<T> clazz) {
        this.logger.debug("--parsing redis entity DAO {}--", clazz.getName());
        if (clazz.isAnnotationPresent(RDaoConfig.class)) {
            //1.获取注解RDaoConfig
            final RDaoConfig rDaoConfig = clazz.getAnnotation(RDaoConfig.class);
            //3.获取实体标识
            final String tableName = rDaoConfig.tableName();
            //5获取该实体所有字段集合
            Field[] fieldTemp = clazz.getDeclaredFields();
            //ColumnHandler
            RColumnHandler keyHandler = null;
            //column
            List<RColumnHandler> columnHandlerList = new ArrayList<RColumnHandler>(fieldTemp.length);
            RColumnHandler columnHandler;
            int modifier;
            String fieldName;
            RColumnConfig columnConfig;
            ColumnTypeEnum columnTypeEnum;
            for (Field field : fieldTemp) {
                modifier = field.getModifiers();
                if (!Modifier.isStatic(modifier)) {
                    //非静态字段
                    fieldName = field.getName();
                    if (field.isAnnotationPresent(RColumnConfig.class)) {
                        //
                        columnConfig = field.getAnnotation(RColumnConfig.class);
                        columnTypeEnum = columnConfig.columnTypeEnum();
                        if (columnTypeEnum == ColumnTypeEnum.KEY) {
                            if (keyHandler == null) {
                                keyHandler = new RColumnHandlerImpl(fieldName, columnTypeEnum, columnConfig.desc(), "-1");
                            } else {
                                throw new RuntimeException("There was an error building REntityDao:" + clazz.getName() + ". Cause:too many key");
                            }
                        } else {
                            columnHandler = new RColumnHandlerImpl(fieldName, columnTypeEnum, columnConfig.desc(), columnConfig.defaultValue());
                            columnHandlerList.add(columnHandler);
                        }
                    }
                }
            }
            if (keyHandler == null) {
                throw new RuntimeException("There was an error building REntityDao:" + clazz.getName() + ". Cause:can not find key");
            }
            REntityDaoBuilder<T> entityDaoBuilder = new REntityDaoBuilder<T>(
                    tableName,
                    keyHandler,
                    columnHandlerList,
                    clazz,
                    this.entityDaoContext);
            REntityDao<T> entityDao = entityDaoBuilder.build();
            entityDaoContext.putREntityDao(clazz, entityDao, tableName);
            this.logger.debug("--parse REntity DAO {} finished--", clazz.getName());
        } else {
            this.logger.error("--parse REntity DAO {} missing annotation RDaoConfig--", clazz.getName());
        }
    }
}