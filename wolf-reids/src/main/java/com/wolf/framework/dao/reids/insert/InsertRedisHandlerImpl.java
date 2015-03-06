package com.wolf.framework.dao.reids.insert;

import com.wolf.framework.dao.AbstractDaoHandler;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.dao.insert.InsertHandler;
import com.wolf.framework.dao.reids.RedisHandler;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public class InsertRedisHandlerImpl<T extends Entity> extends AbstractDaoHandler<T> implements InsertHandler<T> {

    private final RedisHandler redisHandler;

    public InsertRedisHandlerImpl(RedisHandler redisHandler, Class<T> clazz) {
        super(clazz);
        this.redisHandler = redisHandler;
    }

    @Override
    public String insert(Map<String, String> entityMap) {
        return this.redisHandler.insert(entityMap);
    }

    @Override
    public void batchInsert(List<Map<String, String>> entityMapList) {
        this.redisHandler.batchInsert(entityMapList);
    }
}
