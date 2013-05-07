package com.wolf.framework.dao;

import com.wolf.framework.cache.DefaultCacheConfiguration;
import com.wolf.framework.dao.cache.InquireCache;
import com.wolf.framework.dao.cache.InquireCacheImpl;
import com.wolf.framework.dao.delete.DeleteDataHandlerImpl;
import com.wolf.framework.dao.delete.DeleteEntityCacheHandlerImpl;
import com.wolf.framework.dao.delete.DeleteHandler;
import com.wolf.framework.dao.delete.DeleteInquireCacheHandlerImpl;
import com.wolf.framework.dao.inquire.CountByConditionFilterHandlerImpl;
import com.wolf.framework.dao.inquire.CountByConditionFromCacheHandlerImpl;
import com.wolf.framework.dao.inquire.CountByConditionFromDataHandlerImpl;
import com.wolf.framework.dao.inquire.CountByConditionHandler;
import com.wolf.framework.dao.inquire.InquireByConditionFilterHandlerImpl;
import com.wolf.framework.dao.inquire.InquireByConditionFromCacheHandlerImpl;
import com.wolf.framework.dao.inquire.InquireByConditionFromDataHandlerImpl;
import com.wolf.framework.dao.inquire.InquireByConditionHandler;
import com.wolf.framework.dao.inquire.InquireByKeyFilterHandlerImpl;
import com.wolf.framework.dao.inquire.InquireByKeyFromCacheHandlerImpl;
import com.wolf.framework.dao.inquire.InquireByKeyFromDataHandlerImpl;
import com.wolf.framework.dao.inquire.InquireByKeyHandler;
import com.wolf.framework.dao.inquire.InquireKeyByConditionFilterHandlerImpl;
import com.wolf.framework.dao.inquire.InquireKeyByConditionFromCacheHandlerImpl;
import com.wolf.framework.dao.inquire.InquireKeyByConditionFromDataHandlerImpl;
import com.wolf.framework.dao.inquire.InquireKeyByConditionHandler;
import com.wolf.framework.dao.insert.InsertCacheHandlerImpl;
import com.wolf.framework.dao.insert.InsertDataHandlerImpl;
import com.wolf.framework.dao.insert.InsertHandler;
import com.wolf.framework.dao.parser.ColumnHandler;
import com.wolf.framework.dao.update.UpdateDataHandlerImpl;
import com.wolf.framework.dao.update.UpdateEntityCacheHandlerImpl;
import com.wolf.framework.dao.update.UpdateHandler;
import com.wolf.framework.dao.update.UpdateInquireCacheHandlerImpl;
import com.wolf.framework.derby.DerbyHandler;
import com.wolf.framework.derby.DerbyHandlerImpl;
import java.util.ArrayList;
import java.util.List;
import net.sf.ehcache.Cache;
import net.sf.ehcache.config.CacheConfiguration;

/**
 * 实体数据访问对象创建类
 *
 * @author aladdin
 */
public final class EntityDaoBuilder<T extends Entity> {

    //table name
    private final String tableName;
    //key
    private final ColumnHandler keyHandler;
    //column
    private final List<ColumnHandler> columnHandlerList;
    //实体class
    private final Class<T> clazz;
    //是否使用缓存
    private final boolean useCache;
    private final int maxEntriesLocalHeap;
    private final int timeToIdleSeconds;
    private final int timeToLiveSeconds;
    //
    private final EntityDaoContext<T> entityDaoContext;

    public EntityDaoBuilder(String tableName, ColumnHandler keyHandler, List<ColumnHandler> columnHandlerList, Class<T> clazz, boolean useCache, int maxEntriesLocalHeap, int timeToIdleSeconds, int timeToLiveSeconds, EntityDaoContext<T> entityDaoContext) {
        this.tableName = "t_".concat(tableName);
        this.keyHandler = keyHandler;
        this.columnHandlerList = columnHandlerList;
        this.clazz = clazz;
        this.useCache = useCache;
        this.maxEntriesLocalHeap = maxEntriesLocalHeap;
        this.timeToIdleSeconds = timeToIdleSeconds;
        this.timeToLiveSeconds = timeToLiveSeconds;
        this.entityDaoContext = entityDaoContext;
    }

    public EntityDao<T> build() {
        if (this.tableName == null || this.tableName.equals("t_")) {
            throw new RuntimeException("There was an error building entityDao. Cause: tableName is null or empty");
        }
        if (this.clazz == null) {
            throw new RuntimeException("There was an error building entityDao. Cause: clazz is null");
        }
        if (this.keyHandler == null) {
            throw new RuntimeException("There was an error building entityDao. Cause: key is null");
        }
        if (this.columnHandlerList == null || this.columnHandlerList.isEmpty()) {
            throw new RuntimeException("There was an error building entityDao. Cause: columns null or empty");
        }
        //创建查询缓存对象
        final CacheConfiguration inquireCacheConfig = new DefaultCacheConfiguration().getDefault();
        String inquireCacheName = this.tableName.concat("_inquire");
        inquireCacheConfig.name(inquireCacheName).maxEntriesLocalHeap(20000);
        final Cache entityInquireCache = new Cache(inquireCacheConfig);
        entityDaoContext.getCacheManager().addCache(entityInquireCache);
        final InquireCache inquireCache = new InquireCacheImpl(entityInquireCache);
        //初始化实体缓存
        Cache entityCache = null;
        if (this.useCache) {
            //获取实体缓存对象
            if (entityDaoContext.getCacheManager().cacheExists(tableName)) {
                StringBuilder mesBuilder = new StringBuilder(512);
                mesBuilder.append("There was an error parsing entity cache. Cause: exist cache name : ").append(tableName);
                mesBuilder.append("\n").append("error class is ").append(clazz.getName());
                throw new RuntimeException(mesBuilder.toString());
            }
            final CacheConfiguration entityCacheConfig = new DefaultCacheConfiguration().getDefault();
            entityCacheConfig.name(tableName).maxEntriesLocalHeap(maxEntriesLocalHeap)
                    .timeToIdleSeconds(timeToIdleSeconds).timeToLiveSeconds(timeToLiveSeconds);
            entityCache = new Cache(entityCacheConfig);
            entityDaoContext.getCacheManager().addCache(entityCache);
        }
        //初始化derby数据库处理对象
        final DerbyHandler derbyHandler = new DerbyHandlerImpl(this.entityDaoContext.getDataSource(), this.tableName, this.keyHandler, this.columnHandlerList);
        //
        //---------------------------构造根据key查询数据库entity处理对象
        InquireByKeyHandler<T> inquireByKeyHandler = new InquireByKeyFromDataHandlerImpl<T>(
                derbyHandler,
                this.clazz);
        if (entityCache != null) {
            //构造根据key查询缓存处理对象
            inquireByKeyHandler = new InquireByKeyFromCacheHandlerImpl<T>(inquireByKeyHandler, entityCache);
        }
        inquireByKeyHandler = new InquireByKeyFilterHandlerImpl<T>(inquireByKeyHandler);
        //
        //----------------------------------构造数据增、删、改操作对象
        //构造插入数据库处理对象
        InsertHandler<T> insertHandler = new InsertDataHandlerImpl<T>(
                derbyHandler,
                this.clazz,
                this.keyHandler);
        //构造更新数据库处理对象
        UpdateHandler updateHandler = new UpdateDataHandlerImpl(
                derbyHandler,
                this.clazz,
                this.keyHandler);
        //构造删除数据库处理对象
        DeleteHandler deleteHandler = new DeleteDataHandlerImpl(derbyHandler);
        //----------------------------构造删、改实体缓存处理对象
        if (entityCache != null) {
            //构造更新数据时，实体缓存处理对象
            updateHandler = new UpdateEntityCacheHandlerImpl(entityCache, this.keyHandler, updateHandler);
            //构造删除数据时，实体缓存处理对象
            deleteHandler = new DeleteEntityCacheHandlerImpl(entityCache, deleteHandler);
        }
        //----------------------------构造增、删、改查询缓存处理对象
        //构造插入数据缓存处理对象
        insertHandler = new InsertCacheHandlerImpl<T>(inquireCache, insertHandler);
        //构造更新数据缓存处理对象
        updateHandler = new UpdateInquireCacheHandlerImpl(inquireCache, updateHandler);
        //构造删除数据缓存处理对象
        deleteHandler = new DeleteInquireCacheHandlerImpl(inquireCache, deleteHandler);
        //
        //---------------条件过滤对象
        List<ColumnHandler> filterList = new ArrayList<ColumnHandler>(this.columnHandlerList.size() + 1);
        filterList.add(this.keyHandler);
        filterList.addAll(this.columnHandlerList);
        //-----------------------------构造根据条件查询key集合处理对象
        //构造根据条件查询key集合数据库处理对象
        InquireKeyByConditionHandler inquireKeyByConditionHandler = new InquireKeyByConditionFromDataHandlerImpl(derbyHandler);
        //构造根据条件查询key集合缓存处理对象
        inquireKeyByConditionHandler = new InquireKeyByConditionFromCacheHandlerImpl(
                inquireCache,
                inquireKeyByConditionHandler);
        //够在根据条件查询key集合条件过滤对象
        inquireKeyByConditionHandler = new InquireKeyByConditionFilterHandlerImpl(
                inquireKeyByConditionHandler,
                filterList);
        //
        //-----------------------------构造根据条件查询entity集合处理对象
        //根据条件查询数据库entity集合处理对象
        InquireByConditionHandler<T> inquireByConditionHandler = new InquireByConditionFromDataHandlerImpl<T>(
                derbyHandler,
                this.clazz);
        //根据条件查询缓存entity处理对象
        inquireByConditionHandler = new InquireByConditionFromCacheHandlerImpl<T>(
                inquireCache,
                inquireByKeyHandler,
                inquireByConditionHandler);
        //条件查询，有效条件过滤对象
        inquireByConditionHandler = new InquireByConditionFilterHandlerImpl<T>(
                filterList,
                inquireByConditionHandler);
        //----------------------------构造根据条件查询总记录数处理对象
        //根据条件查询总记录数处理对象
        CountByConditionHandler countByConditionHandler = new CountByConditionFromDataHandlerImpl(derbyHandler);
        //根据条件查询总记录数处理对象
        countByConditionHandler = new CountByConditionFromCacheHandlerImpl(
                inquireCache,
                countByConditionHandler);
        //条件查询，有效条件过滤对象
        countByConditionHandler = new CountByConditionFilterHandlerImpl(
                filterList,
                countByConditionHandler);
        EntityDao<T> entityDao = new EntityDaoImpl(
                insertHandler,
                updateHandler,
                deleteHandler,
                inquireByKeyHandler,
                inquireByConditionHandler,
                inquireKeyByConditionHandler,
                countByConditionHandler);
        return entityDao;
    }
}
