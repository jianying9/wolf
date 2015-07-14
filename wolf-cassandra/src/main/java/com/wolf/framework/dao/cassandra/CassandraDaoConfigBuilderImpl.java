package com.wolf.framework.dao.cassandra;

import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.dao.ColumnHandler;
import com.wolf.framework.dao.ColumnHandlerImpl;
import com.wolf.framework.dao.ColumnType;
import com.wolf.framework.dao.DaoConfig;
import com.wolf.framework.dao.DaoConfigBuilder;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.dao.cassandra.annotation.ColumnConfig;
import com.wolf.framework.dao.cassandra.annotation.CDaoConfig;
import com.wolf.framework.injecter.Injecter;
import com.wolf.framework.logger.LogFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

/**
 *
 * @author jianying9
 * @param <T>
 */
@DaoConfig()
public class CassandraDaoConfigBuilderImpl<T extends Entity> implements DaoConfigBuilder {

    private final Logger logger = LogFactory.getLogger(FrameworkLogger.FRAMEWORK);
    private final List<Class<T>> cEntityClassList = new ArrayList<Class<T>>();
    private CEntityDaoContext<T> cEntityDaoContext;
    private CassandraAdminContext cassandraAdminContext;

    @Override
    public void init(ApplicationContext context) {
        this.cEntityDaoContext = new CEntityDaoContextImpl<T>();
        this.cassandraAdminContext = new CassandraAdminContextImpl(context);
    }

    @Override
    public Class<?> getAnnotation() {
        return CDaoConfig.class;
    }

    @Override
    public void putClazz(Class<?> clazz) {
        //是否是实体
        if (Entity.class.isAssignableFrom(clazz)) {
            Class<T> clazzt = (Class<T>) clazz;
            if (clazzt.isAnnotationPresent(CDaoConfig.class)) {
                if (this.cEntityClassList.contains(clazzt) == false) {
                    this.cEntityClassList.add(clazzt);
                    this.logger.debug("find cassandra entity class ".concat(clazz.getName()));
                }
            }
        }
    }

    @Override
    public Injecter getInjecter() {
        return new CDaoInjecterImpl(this.cEntityDaoContext);
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
        //解析cassandra EntityDao
        if (this.cEntityClassList.isEmpty() == false) {
            this.logger.info("parsing annotation CDaoConfig...");
            for (Class<T> clazz : this.cEntityClassList) {
                this.logger.debug("--parsing cassandra entity DAO {}--", clazz.getName());
                if (clazz.isAnnotationPresent(CDaoConfig.class)) {
                    //获取注解RDaoConfig
                    final CDaoConfig cDaoConfig = clazz.getAnnotation(CDaoConfig.class);
                    //表空间
                    final String keyspace = cDaoConfig.keyspace();
                    //表
                    final String table = cDaoConfig.table();
                    //是否计数表
                    final boolean counter = cDaoConfig.counter();
                    String dataMap;
                    //set类型集合
//                    Map<String, String> sets = new HashMap<String, String>(4, 1);
//                    for (String name : cDaoConfig.sets()) {
//                        dataMap = this.getDefaultDataMap(name);
//                        sets.put(name, dataMap);
//                    }
                    //list类型集合
//                    Map<String, String> lists = new HashMap<String, String>(4, 1);
//                    for (String name : cDaoConfig.lists()) {
//                        dataMap = this.getDefaultDataMap(name);
//                        sets.put(name, dataMap);
//                    }
                    //map类型集合
//                    Map<String, String> maps = new HashMap<String, String>(4, 1);
//                    for (String name : cDaoConfig.maps()) {
//                        dataMap = this.getDefaultDataMap(name);
//                        sets.put(name, dataMap);
//                    }
                    //获取该实体所有字段集合
                    Field[] fieldTemp = clazz.getDeclaredFields();
                    //ColumnHandler
                    List<ColumnHandler> keyHandlerList = new ArrayList<ColumnHandler>(1);
                    //column
                    List<ColumnHandler> columnHandlerList = new ArrayList<ColumnHandler>(0);
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
                    if (keyHandlerList.isEmpty()) {
                        throw new RuntimeException("Error building CEntityDao:" + clazz.getName() + ". Cause:can not find key");
                    }
                    CEntityDaoBuilder<T> entityDaoBuilder = new CEntityDaoBuilder<T>(
                            keyspace,
                            table,
                            counter,
                            keyHandlerList,
                            columnHandlerList,
                            clazz,
                            this.cEntityDaoContext,
                            this.cassandraAdminContext
                    );
                    CEntityDao<T> entityDao = entityDaoBuilder.build();
                    this.cEntityDaoContext.putCEntityDao(clazz, entityDao, table);
                    this.logger.debug("--parse CEntity DAO {} finished--", clazz.getName());
                } else {
                    this.logger.error("--parse CEntity DAO {} missing annotation CDaoConfig--", clazz.getName());
                }
            }
        }
    }
}
