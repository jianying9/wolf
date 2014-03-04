package com.wolf.framework.dao.delete;

import com.wolf.framework.redis.RedisHandler;
import java.util.List;

/**
 *
 * @author aladdin
 */
public class DeleteRedisHandlerImpl implements DeleteHandler {
    
    private final RedisHandler redisHandler;

    public DeleteRedisHandlerImpl(RedisHandler redisHandler) {
        this.redisHandler = redisHandler;
    }
    
    @Override
    public void delete(String keyValue) {
        this.redisHandler.delete(keyValue);
    }

    @Override
    public void batchDelete(List<String> keyValues) {
        if(keyValues.isEmpty() == false) {
            this.redisHandler.batchDelete(keyValues);
        }
    }
}
