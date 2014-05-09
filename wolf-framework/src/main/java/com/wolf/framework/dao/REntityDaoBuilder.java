package com.wolf.framework.dao;

import com.wolf.framework.dao.delete.DeleteHandler;
import com.wolf.framework.dao.delete.DeleteRedisHandlerImpl;
import com.wolf.framework.dao.inquire.InquireByKeyFilterHandlerImpl;
import com.wolf.framework.dao.inquire.InquireByKeyFromRedisHandlerImpl;
import com.wolf.framework.dao.inquire.InquireByKeyHandler;
import com.wolf.framework.dao.insert.InsertHandler;
import com.wolf.framework.dao.insert.InsertRedisHandlerImpl;
import com.wolf.framework.dao.parser.RColumnHandler;
import com.wolf.framework.dao.update.UpdateHandler;
import com.wolf.framework.dao.update.UpdateRedisHandlerImpl;
import com.wolf.framework.redis.RedisHandler;
import com.wolf.framework.redis.RedisHandlerImpl;
import java.util.ArrayList;
import java.util.List;

/**
 * 实体数据访问对象创建类
 *
 * @author aladdin
 */
public final class REntityDaoBuilder<T extends Entity> {

    //table name
    private final String tableName;
    //
    private final int dbIndex;
    //key
    private final RColumnHandler keyHandler;
    //column
    private final List<RColumnHandler> columnHandlerList;
    //实体class
    private final Class<T> clazz;
    private final REntityDaoContext<T> entityDaoContext;

    public REntityDaoBuilder(String tableName, int dbIndex, RColumnHandler keyHandler, List<RColumnHandler> columnHandlerList, Class<T> clazz, REntityDaoContext<T> entityDaoContext) {
        this.tableName = tableName;
        this.dbIndex = dbIndex;
        this.keyHandler = keyHandler;
        if (columnHandlerList == null) {
            this.columnHandlerList = new ArrayList<RColumnHandler>(0);
        } else {
            this.columnHandlerList = columnHandlerList;
        }
        this.clazz = clazz;
        this.entityDaoContext = entityDaoContext;
    }

    public REntityDao<T> build() {
        if (this.tableName == null || this.tableName.equals("t_")) {
            throw new RuntimeException("Error when building REntityDao. Cause: tableName is null or empty");
        }
        if (this.dbIndex == 0) {
            throw new RuntimeException("Error when building REntityDao. Cause: dbIndex must > 0");
        }
        if (this.clazz == null) {
            throw new RuntimeException("Error when building REntityDao. Cause: clazz is null");
        }
        if (this.keyHandler == null) {
            throw new RuntimeException("Error when building REntityDao. Cause: key is null");
        }
        //初始化redis数据库处理对象
        final RedisHandler redisHandler = new RedisHandlerImpl(this.tableName, this.dbIndex, this.entityDaoContext.getJedisPool(), this.keyHandler, this.columnHandlerList);
        //---------------------------构造根据key查询数据库entity处理对象
        InquireByKeyHandler<T> inquireByKeyHandler = new InquireByKeyFromRedisHandlerImpl<T>(
                redisHandler,
                this.clazz,
                this.columnHandlerList);
        inquireByKeyHandler = new InquireByKeyFilterHandlerImpl<T>(inquireByKeyHandler);
        //
        //----------------------------------构造数据增、删、改操作对象
        //构造插入数据库处理对象
        InsertHandler<T> insertHandler = new InsertRedisHandlerImpl<T>(
                redisHandler,
                this.clazz);
        //构造更新数据库处理对象
        UpdateHandler updateHandler = new UpdateRedisHandlerImpl(
                redisHandler,
                this.clazz);
        //构造删除数据库处理对象
        DeleteHandler deleteHandler = new DeleteRedisHandlerImpl(redisHandler);
        REntityDao<T> entityDao = new REntityDaoImpl(
                insertHandler,
                updateHandler,
                deleteHandler,
                inquireByKeyHandler,
                redisHandler);
        return entityDao;
    }
}
