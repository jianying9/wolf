package com.wolf.framework.dao.elasticsearch;

import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.dao.ColumnHandler;
import com.wolf.framework.dao.ColumnType;
import com.wolf.framework.dao.DaoConfig;
import com.wolf.framework.dao.DaoConfigBuilder;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.dao.elasticsearch.annotation.EsColumnConfig;
import com.wolf.framework.injecter.Injecter;
import com.wolf.framework.logger.LogFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import com.wolf.framework.dao.elasticsearch.annotation.EsEntityConfig;
import java.util.Map;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author jianying9
 * @param <T>
 */
@DaoConfig()
public class EsEntityConfigBuilderImpl<T extends Entity> implements DaoConfigBuilder
{

    private final Logger logger = LogFactory.getLogger(FrameworkLogger.DAO);
    private final List<Class<T>> esEntityClassList = new ArrayList();
    private EsContext esContext;
    private Map<Class<?>, List<ColumnHandler>> entityInfoMap;

    @Override
    public void init(ApplicationContext context, Map<Class<?>, List<ColumnHandler>> entityInfoMap)
    {
        this.esContext = EsContextImpl.getInstance(context);
        this.entityInfoMap = entityInfoMap;
    }

    @Override
    public Class<?> getAnnotation()
    {
        return EsEntityConfig.class;
    }

    @Override
    public void putClazz(Class<?> clazz)
    {
        //是否是实体
        if (Entity.class.isAssignableFrom(clazz)) {
            Class<T> clazzt = (Class<T>) clazz;
            if (clazzt.isAnnotationPresent(EsEntityConfig.class)) {
                if (this.esEntityClassList.contains(clazzt) == false) {
                    this.esEntityClassList.add(clazzt);
                    this.logger.debug("find elasticsearch entity class ".concat(clazz.getName()));
                }
            }
        }
    }

    @Override
    public Injecter getInjecter()
    {
        return new EsEntityDaoInjecterImpl(this.esContext);
    }

    @Override
    public void build()
    {
        //解析 EsEntityDao
        if (this.esEntityClassList.isEmpty() == false) {
            this.logger.info("parsing annotation EsEntityDaoConfig...");
            for (Class<T> clazz : this.esEntityClassList) {
                this.logger.debug("--parsing EsEntityDao {}--", clazz.getName());
                if (clazz.isAnnotationPresent(EsEntityConfig.class)) {
                    //获取注解RDaoConfig
                    final EsEntityConfig esDaoConfig = clazz.getAnnotation(EsEntityConfig.class);
                    //表
                    final String table = esDaoConfig.table();
                    String type = esDaoConfig.type();
                    final boolean multiCompile = esDaoConfig.multiComplie();
                    if (type.isEmpty()) {
                        type = table;
                    }
                    //
                    //获取该实体所有字段集合
                    Field[] fieldTemp = clazz.getDeclaredFields();
                    //ColumnHandler
                    EsColumnHandler keyHandler = null;
                    //column
                    List<ColumnHandler> columnHandlerList = new ArrayList(0);
                    EsColumnHandler columnHandler;
                    int modifier;
                    String fieldName;
                    EsColumnConfig esColumnConfig;
                    ColumnType columnType;
                    for (Field field : fieldTemp) {
                        modifier = field.getModifiers();
                        if (Modifier.isStatic(modifier) == false) {
                            //非静态字段
                            fieldName = field.getName();
                            if (field.isAnnotationPresent(EsColumnConfig.class)) {
                                //
                                esColumnConfig = field.getAnnotation(EsColumnConfig.class);
                                columnType = esColumnConfig.columnType();
                                if (columnType == ColumnType.KEY) {
                                    keyHandler = new EsColumnHandlerImpl(fieldName, fieldName, field, columnType, esColumnConfig.desc(), "");
                                } else {
                                    columnHandler = new EsColumnHandlerImpl(esColumnConfig.analyzer(), fieldName, fieldName, field, columnType, esColumnConfig.desc(), esColumnConfig.defaultValue());
                                    columnHandlerList.add(columnHandler);
                                }
                            }
                        }
                    }
                    if (keyHandler != null) {
                        //缓存所有columnHandler
                        List<ColumnHandler> allColumnHandlerList = new ArrayList(1 + columnHandlerList.size());
                        allColumnHandlerList.add(keyHandler);
                        allColumnHandlerList.addAll(columnHandlerList);
                        this.entityInfoMap.put(clazz, allColumnHandlerList);
                        //
                        EsEntityDaoBuilder<T> entityDaoBuilder = new EsEntityDaoBuilder(
                                table,
                                type,
                                multiCompile,
                                keyHandler,
                                columnHandlerList,
                                clazz,
                                this.esContext
                        );
                        EsEntityDao<T> entityDao = entityDaoBuilder.build();
                        this.esContext.putEsEntityDao(clazz, entityDao, multiCompile, table);
                        this.logger.debug("--parse EsEntityDao {} finished--", clazz.getName());
                    } else {
                        this.logger.error("--parse EsEntityDao {} missing key column--", clazz.getName());
                    }
                } else {
                    this.logger.error("--parse EsEntityDao {} missing annotation EsEntityDaoConfig--", clazz.getName());
                }
            }
        }
    }
}
