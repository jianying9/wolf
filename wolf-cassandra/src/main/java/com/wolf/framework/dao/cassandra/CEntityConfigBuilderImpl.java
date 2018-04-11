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
import com.wolf.framework.injecter.Injecter;
import com.wolf.framework.logger.LogFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import com.wolf.framework.dao.cassandra.annotation.CEntityConfig;
import java.util.HashMap;
import java.util.Map;
import net.sf.ehcache.Cache;

/**
 *
 * @author jianying9
 * @param <T>
 */
@DaoConfig()
public class CEntityConfigBuilderImpl<T extends Entity> implements DaoConfigBuilder {

    private final Logger logger = LogFactory.getLogger(FrameworkLogger.DAO);
    private final List<Class<T>> cEntityClassList = new ArrayList<>();
    private CassandraAdminContext cassandraAdminContext;
    private Map<Class<?>, List<ColumnHandler>> entityInfoMap;
    private Cache escache;

    @Override
    public void init(ApplicationContext context, Map<Class<?>, List<ColumnHandler>> entityInfoMap) {
        this.cassandraAdminContext = CassandraAdminContextImpl.getInstance(context);
        this.entityInfoMap = entityInfoMap;
        this.escache = context.getCache();
    }

    @Override
    public Class<?> getAnnotation() {
        return CEntityConfig.class;
    }

    @Override
    public void putClazz(Class<?> clazz) {
        //是否是实体
        if (Entity.class.isAssignableFrom(clazz)) {
            Class<T> clazzt = (Class<T>) clazz;
            if (clazzt.isAnnotationPresent(CEntityConfig.class)) {
                if (this.cEntityClassList.contains(clazzt) == false) {
                    this.cEntityClassList.add(clazzt);
                    this.logger.debug("find cassandra entity class ".concat(clazz.getName()));
                }
            }
        }
    }

    @Override
    public Injecter getInjecter() {
        return new CEntityDaoInjecterImpl(this.cassandraAdminContext);
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
            this.logger.info("parsing annotation CEntityDaoConfig...");
            for (Class<T> clazz : this.cEntityClassList) {
                this.logger.debug("--parsing cassandra CEntityDao {}--", clazz.getName());
                if (clazz.isAnnotationPresent(CEntityConfig.class)) {
                    //获取注解RDaoConfig
                    final CEntityConfig cDaoConfig = clazz.getAnnotation(CEntityConfig.class);
                    //表空间
                    final String keyspace = cDaoConfig.keyspace();
                    //表
                    final String table = cDaoConfig.table();
                    //缓存
                    boolean cache = cDaoConfig.cache();
                    //
                    String dataMap;
                    //set类型集合
                    Map<String, String> setNames = new HashMap<>(2, 1);
                    for (String name : cDaoConfig.sets()) {
                        dataMap = this.getDefaultDataMap(name);
                        setNames.put(name, dataMap);
                    }
                    //list类型集合
                    Map<String, String> listNames = new HashMap<>(2, 1);
                    for (String name : cDaoConfig.lists()) {
                        dataMap = this.getDefaultDataMap(name);
                        listNames.put(name, dataMap);
                    }
                    //map类型集合
                    Map<String, String> mapNames = new HashMap<>(2, 1);
                    for (String name : cDaoConfig.maps()) {
                        dataMap = this.getDefaultDataMap(name);
                        mapNames.put(name, dataMap);
                    }
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
                    CEntityDaoBuilder<T> entityDaoBuilder = new CEntityDaoBuilder<>(
                            keyspace,
                            table,
                            keyHandlerList,
                            columnHandlerList,
                            setNames,
                            listNames,
                            mapNames,
                            clazz,
                            this.cassandraAdminContext,
                            cache,
                            escache
                    );
                    CEntityDao<T> entityDao = entityDaoBuilder.build();
                    this.cassandraAdminContext.putCEntityDao(clazz, entityDao, keyspace, table);
                    this.logger.debug("--parse CEntityDao {} finished--", clazz.getName());
                } else {
                    this.logger.error("--parse CEntityDao {} missing annotation CEntityDaoConfig--", clazz.getName());
                }
            }
        }
    }
}
