package com.wolf.framework.dao.inquire;

import com.wolf.framework.dao.AbstractDaoHandler;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.redis.RedisHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public final class InquireByKeyFromRedisHandlerImpl<T extends Entity> extends AbstractDaoHandler<T> implements InquireByKeyHandler<T> {

    private final RedisHandler redisHandler;

    public InquireByKeyFromRedisHandlerImpl(RedisHandler redisHandler, Class<T> clazz) {
        super(clazz);
        this.redisHandler = redisHandler;
    }

    @Override
    public T inquireByKey(String keyValue) {
        T t = null;
        Map<String, String> entityMap = this.redisHandler.inquireByKey(keyValue);
        if (entityMap != null) {
            t = this.newInstance(entityMap);
        }
        return t;
    }

    @Override
    public List<T> inquireByKeys(List<String> keyValues) {
        List<T> tList;
        List<Map<String, String>> entityMapList = this.redisHandler.inquireBykeys(keyValues);
        if (keyValues.isEmpty() == false) {
            tList = this.newInstance(entityMapList);
        } else {
            tList = new ArrayList<T>(0);
        }
        return tList;
    }
}
