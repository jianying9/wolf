package com.wolf.framework.dao.reids;

import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.dao.ColumnHandler;
import com.wolf.framework.dao.ColumnHandlerImpl;
import com.wolf.framework.dao.ColumnType;
import com.wolf.framework.dao.DaoConfig;
import com.wolf.framework.dao.DaoConfigBuilder;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.dao.reids.annotation.ColumnConfig;
import com.wolf.framework.dao.reids.annotation.RDaoConfig;
import com.wolf.framework.injecter.Injecter;
import com.wolf.framework.logger.LogFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;

/**
 *
 * @author jianying9
 * @param <T>
 */
@DaoConfig()
public class RedisDaoConfigBuilderImpl<T extends Entity> implements DaoConfigBuilder {

    private final Logger logger = LogFactory.getLogger(FrameworkLogger.FRAMEWORK);
    private final List<Class<T>> rEntityClassList = new ArrayList<Class<T>>();
    private RedisAdminContext redisAdminContext;
    private REntityDaoContext<T> rEntityDaoContext;

    @Override
    public void init(ApplicationContext context) {
        this.redisAdminContext = new RedisAdminContextImpl(context);
        this.rEntityDaoContext = new REntityDaoContextImpl<T>();
        TestRedisHandler.redisAdminContext = this.redisAdminContext;
    }

    @Override
    public Class<?> getAnnotation() {
        return RDaoConfig.class;
    }

    @Override
    public void putClazz(Class<?> clazz) {
        //是否是实体
        if (Entity.class.isAssignableFrom(clazz)) {
            Class<T> clazzt = (Class<T>) clazz;
            if (clazzt.isAnnotationPresent(RDaoConfig.class)) {
                if (this.rEntityClassList.contains(clazzt) == false) {
                    this.rEntityClassList.add(clazzt);
                    this.logger.debug("find redis entity class ".concat(clazz.getName()));
                }
            }
        }
    }

    @Override
    public Injecter getInjecter() {
        return new RDaoInjecterImpl(this.rEntityDaoContext);
    }

    @Override
    public void build() {
//        解析redis EntityDao
        if (this.rEntityClassList.isEmpty() == false) {
            this.logger.info("parsing annotation RDaoConfig...");
            for (Class<T> clazzt : this.rEntityClassList) {
                this.logger.debug("--parsing redis entity DAO {}--", clazzt.getName());
                if (clazzt.isAnnotationPresent(RDaoConfig.class)) {
                    //获取注解RDaoConfig
                    final RDaoConfig rDaoConfig = clazzt.getAnnotation(RDaoConfig.class);
                    //获取实体标识
                    final String tableName = rDaoConfig.tableName();
                    //获取sortedSets
                    String[] sortedSets = rDaoConfig.sortedSets();
                    final Set<String> sortedSetNames = new HashSet<String>(sortedSets.length, 1);
                    sortedSetNames.addAll(Arrays.asList(sortedSets));
                    //获取该实体所有字段集合
                    Field[] fieldTemp = clazzt.getDeclaredFields();
                    //ColumnHandler
                    ColumnHandler keyHandler = null;
                    //column
                    List<ColumnHandler> columnHandlerList = new ArrayList<ColumnHandler>(fieldTemp.length);
                    ColumnHandler columnHandler;
                    int modifier;
                    String fieldName;
                    ColumnConfig columnConfig;
                    ColumnType columnType;
                    for (Field field : fieldTemp) {
                        modifier = field.getModifiers();
                        if (!Modifier.isStatic(modifier)) {
                            //非静态字段
                            fieldName = field.getName();
                            if (field.isAnnotationPresent(ColumnConfig.class)) {
                                //
                                columnConfig = field.getAnnotation(ColumnConfig.class);
                                columnType = columnConfig.columnType();
                                if (columnType == ColumnType.KEY) {
                                    if (keyHandler == null) {
                                        keyHandler = new ColumnHandlerImpl(fieldName, fieldName, field, columnType, columnConfig.desc(), "-1");
                                    } else {
                                        throw new RuntimeException("Error building REntityDao:" + clazzt.getName() + ". Cause:too many key");
                                    }
                                } else {
                                    columnHandler = new ColumnHandlerImpl(fieldName, fieldName, field, columnType, columnConfig.desc(), columnConfig.defaultValue());
                                    columnHandlerList.add(columnHandler);
                                }
                            }
                        }
                    }
                    if (keyHandler == null) {
                        throw new RuntimeException("Error building REntityDao:" + clazzt.getName() + ". Cause:can not find key");
                    }
                    REntityDaoBuilder<T> entityDaoBuilder = new REntityDaoBuilder<T>(
                            tableName,
                            keyHandler,
                            columnHandlerList,
                            sortedSetNames,
                            clazzt,
                            this.redisAdminContext);
                    REntityDao<T> entityDao = entityDaoBuilder.build();
                    this.rEntityDaoContext.putREntityDao(clazzt, entityDao, tableName);
                    this.logger.debug("--parse REntity DAO {} finished--", clazzt.getName());
                } else {
                    this.logger.error("--parse REntity DAO {} missing annotation RDaoConfig--", clazzt.getName());
                }
            }
        }
    }
}
