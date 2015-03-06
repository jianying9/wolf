package com.wolf.framework.dao.reids.update;

import com.wolf.framework.dao.AbstractDaoHandler;
import com.wolf.framework.dao.update.UpdateHandler;
import com.wolf.framework.dao.reids.RedisHandler;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public class UpdateRedisHandlerImpl extends AbstractDaoHandler implements UpdateHandler {

    private final RedisHandler redisHandler;

    public UpdateRedisHandlerImpl(RedisHandler redisHandler, Class clazz) {
        super(clazz);
        this.redisHandler = redisHandler;
    }

    @Override
    public String update(Map<String, String> entityMap) {
        return this.redisHandler.update(entityMap);
    }

    @Override
    public void batchUpdate(List<Map<String, String>> entityMapList) {
        this.redisHandler.batchUpdate(entityMapList);
    }
}
