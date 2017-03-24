package com.wolf.framework.dao.cassandra;

import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.dao.ColumnHandler;
import com.wolf.framework.dao.ColumnHandlerImpl;
import com.wolf.framework.dao.ColumnType;
import com.wolf.framework.dao.DaoConfig;
import com.wolf.framework.dao.DaoConfigBuilder;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.dao.cassandra.annotation.CCounterConfig;
import com.wolf.framework.dao.cassandra.annotation.ColumnConfig;
import com.wolf.framework.injecter.Injecter;
import com.wolf.framework.logger.LogFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

/**
 *
 * @author jianying9
 * @param <T>
 */
@DaoConfig()
public class CCounterConfigBuilderImpl<T extends Entity> implements DaoConfigBuilder {

    private final Logger logger = LogFactory.getLogger(FrameworkLogger.DAO);
    private final List<Class<T>> cEntityClassList = new ArrayList<>();
    private CassandraAdminContext cassandraAdminContext;
    private Map<Class<?>, List<ColumnHandler>> entityInfoMap;

    @Override
    public void init(ApplicationContext context, Map<Class<?>, List<ColumnHandler>> entityInfoMap) {
        this.cassandraAdminContext = CassandraAdminContextImpl.getInstance(context);
        this.entityInfoMap = entityInfoMap;
    }

    @Override
    public Class<?> getAnnotation() {
        return CCounterConfig.class;
    }

    @Override
    public void putClazz(Class<?> clazz) {
        //是否是实体
        if (Entity.class.isAssignableFrom(clazz)) {
            Class<T> clazzt = (Class<T>) clazz;
            if (clazzt.isAnnotationPresent(CCounterConfig.class)) {
                if (this.cEntityClassList.contains(clazzt) == false) {
                    this.cEntityClassList.add(clazzt);
                    this.logger.debug("find cassandra entity class ".concat(clazz.getName()));
                }
            }
        }
    }

    @Override
    public Injecter getInjecter() {
        return new CCounterDaoInjecterImpl(this.cassandraAdminContext);
    }

    private String getDefaultDataMap(String columnName) {
        StringBuilder sb = new StringBuilder(columnName.length() + 5);
        char[] chs = columnName.toCharArray();
        for (char ch : chs) {
            if (ch >= 65 && ch <= 90) {
                if (sb.length() > 0) {
                    sb.append('_');
                }
                ch = (char) (ch + 32);
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    @Override
    public void build() {
        //解析cassandra CounterDao
        if (this.cEntityClassList.isEmpty() == false) {
            this.logger.info("parsing annotation CCounterConfig...");
            for (Class<T> clazz : this.cEntityClassList) {
                this.logger.debug("--parsing cassandra CCounterDao {}--", clazz.getName());
                if (clazz.isAnnotationPresent(CCounterConfig.class)) {
                    //获取注解RDaoConfig
                    final CCounterConfig cDaoConfig = clazz.getAnnotation(CCounterConfig.class);
                    //表空间
                    final String keyspace = cDaoConfig.keyspace();
                    //表
                    final String table = cDaoConfig.table();
                    String dataMap;
                    //获取该实体所有字段集合
                    Field[] fieldTemp = clazz.getDeclaredFields();
                    //ColumnHandler
                    List<ColumnHandler> keyHandlerList = new ArrayList<>(1);
                    //column
                    List<ColumnHandler> columnHandlerList = new ArrayList<>(0);
                    ColumnHandler columnHandler;
                    int modifier;
                    String fieldName;
                    ColumnConfig columnConfig;
                    ColumnType columnType;
                    for (Field field : fieldTemp) {
                        modifier = field.getModifiers();
                        if (Modifier.isStatic(modifier) == false) {
                            //非静态字段
                            fieldName = field.getName();
                            if (field.isAnnotationPresent(ColumnConfig.class)) {
                                //
                                columnConfig = field.getAnnotation(ColumnConfig.class);
                                dataMap = columnConfig.dataMap();
                                if (dataMap.isEmpty()) {
                                    //大写转小写，并加下划线
                                    dataMap = this.getDefaultDataMap(fieldName);
                                }
                                columnType = columnConfig.columnType();
                                if (columnType == ColumnType.KEY) {
                                    columnHandler = new ColumnHandlerImpl(fieldName, dataMap, field, columnType, columnConfig.desc(), "");
                                    keyHandlerList.add(columnHandler);
                                } else {
                                    columnHandler = new ColumnHandlerImpl(fieldName, dataMap, field, columnType, columnConfig.desc(), columnConfig.defaultValue());
                                    columnHandlerList.add(columnHandler);
                                }
                            }
                        }
                    }
                    //缓存所有columnHandler
                    List<ColumnHandler> allColumnHandlerList = new ArrayList(keyHandlerList.size() + columnHandlerList.size());
                    allColumnHandlerList.addAll(keyHandlerList);
                    allColumnHandlerList.addAll(columnHandlerList);
                    this.entityInfoMap.put(clazz, allColumnHandlerList);
                    //
                    CCounterDaoBuilder<T> entityDaoBuilder = new CCounterDaoBuilder<>(
                            keyspace,
                            table,
                            keyHandlerList,
                            columnHandlerList,
                            clazz,
                            this.cassandraAdminContext
                    );
                    CCounterDao<T> entityDao = entityDaoBuilder.build();
                    this.cassandraAdminContext.putCCounterDao(clazz, entityDao, keyspace, table);
                    this.logger.debug("--parse CCounterDao {} finished--", clazz.getName());
                } else {
                    this.logger.error("--parse CCounterDao {} missing annotation CCounterConfig--", clazz.getName());
                }
            }
        }
    }
}
