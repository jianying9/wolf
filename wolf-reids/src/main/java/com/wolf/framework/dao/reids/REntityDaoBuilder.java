package com.wolf.framework.dao.reids;

import com.wolf.framework.dao.ColumnHandler;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.dao.inquire.InquireByKeyFilterHandlerImpl;
import com.wolf.framework.dao.inquire.InquireByKeyFromDatabaseHandlerImpl;
import com.wolf.framework.dao.inquire.InquireByKeyHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 实体数据访问对象创建类
 *
 * @author aladdin
 * @param <T>
 */
public final class REntityDaoBuilder<T extends Entity> {

    //table name
    private final String tableName;
    //key
    private final ColumnHandler keyHandler;
    //sortedSetNames
    private final Set<String> sortedSetNames;
    //column
    private final List<ColumnHandler> columnHandlerList;
    //实体class
    private final Class<T> clazz;
    private final REntityDaoContext<T> entityDaoContext;

    public REntityDaoBuilder(String tableName, ColumnHandler keyHandler, List<ColumnHandler> columnHandlerList, Set<String> sortedSetNames, Class<T> clazz, REntityDaoContext<T> entityDaoContext) {
        this.tableName = tableName;
        this.keyHandler = keyHandler;
        if (columnHandlerList == null) {
            this.columnHandlerList = new ArrayList<ColumnHandler>(0);
        } else {
            this.columnHandlerList = columnHandlerList;
        }
        this.sortedSetNames = sortedSetNames;
        this.clazz = clazz;
        this.entityDaoContext = entityDaoContext;
    }

    public REntityDao<T> build() {
        if (this.tableName == null) {
            throw new RuntimeException("Error when building REntityDao. Cause: tableName is null or empty");
        }
        if (this.clazz == null) {
            throw new RuntimeException("Error when building REntityDao. Cause: clazz is null");
        }
        if (this.keyHandler == null) {
            throw new RuntimeException("Error when building REntityDao. Cause: key is null");
        }
        //初始化redis数据库处理对象
        final RedisAdminContext redisAdminContext = this.entityDaoContext.getRedisAdminContext();
        final RedisHandler redisHandler = new RedisHandlerImpl(this.tableName, redisAdminContext.getJedisPool(), this.keyHandler, this.columnHandlerList, this.sortedSetNames);
        redisAdminContext.putRedisHandler(this.clazz, redisHandler, this.tableName);
        //---------------------------构造根据key查询数据库entity处理对象
        InquireByKeyHandler<T> inquireByKeyHandler = new InquireByKeyFromDatabaseHandlerImpl<T>(
                redisHandler,
                this.clazz,
                this.columnHandlerList);
        inquireByKeyHandler = new InquireByKeyFilterHandlerImpl<T>(inquireByKeyHandler);
        //
        //----------------------------------构造数据增、删、改操作对象
        REntityDao<T> entityDao = new REntityDaoImpl(
                redisHandler,
                redisHandler,
                redisHandler,
                inquireByKeyHandler,
                redisHandler);
        return entityDao;
    }
}
