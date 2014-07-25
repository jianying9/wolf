package com.wolf.framework.test;

import com.wolf.framework.config.FrameworkConfig;
import com.wolf.framework.config.FrameworkLoggerEnum;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.context.ApplicationContextBuilder;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.redis.RedisAdminContext;
import com.wolf.framework.redis.RedisHandler;
import com.wolf.framework.session.Session;
import com.wolf.framework.utils.SecurityUtils;
import com.wolf.framework.worker.context.Response;
import com.wolf.framework.worker.ServiceWorker;
import com.wolf.framework.worker.context.LocalWorkerContextImpl;
import com.wolf.framework.worker.context.WorkerContext;
import java.util.Map;
import org.slf4j.Logger;

/**
 *
 * @author aladdin
 */
public final class TestHandler {

    private Session session;

    public TestHandler(Map<String, String> parameterMap) {
        synchronized (TestHandler.class) {
            if (ApplicationContext.CONTEXT.isReady() == false) {
                parameterMap.put(FrameworkConfig.COMPILE_MODEL, FrameworkConfig.UNIT_TEST);
                ApplicationContextBuilder applicationContextBuilder = new ApplicationContextBuilder(parameterMap);
                applicationContextBuilder.build();
            }
        }
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Response execute(String act, Map<String, String> parameterMap) {
        Response result;
        ServiceWorker serviceWorker = ApplicationContext.CONTEXT.getServiceWorker(act);
        if (serviceWorker == null) {
            Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.FRAMEWORK);
            logger.error("timer:Can not find act:".concat(act));
            result = null;
        } else {
            String key = ApplicationContext.CONTEXT.getParameter(FrameworkConfig.SEED_DES_KEY);
            String seed = Long.toString(System.currentTimeMillis());
            byte[] entrySeedByte = SecurityUtils.encryptByDes(seed, key);
            String engrySeedHex = SecurityUtils.byteToHexString(entrySeedByte);
            parameterMap.put("seed", engrySeedHex);
            WorkerContext workerContext = new LocalWorkerContextImpl(this.session, act, parameterMap);
            serviceWorker.doWork(workerContext);
            result = serviceWorker.getResponse();
        }
        return result;
    }

    /**
     * 清空某张redis表
     *
     * @param <T>
     * @param clazz
     */
    public <T extends Entity> void truncateRedis(Class<T> clazz) {
        RedisAdminContext redisAdminContext = ApplicationContext.CONTEXT.getRedisAdminContext();
        RedisHandler redisHandler = redisAdminContext.getRedisHandler(clazz);
        if (redisHandler != null) {
            redisHandler.truncate();
        }
    }

    /**
     * 往指定的redis表插入某行记录
     *
     * @param <T>
     * @param clazz
     * @param entityMap
     */
    public <T extends Entity> void insertRedis(Class<T> clazz, Map<String, String> entityMap) {
        RedisAdminContext redisAdminContext = ApplicationContext.CONTEXT.getRedisAdminContext();
        RedisHandler redisHandler = redisAdminContext.getRedisHandler(clazz);
        if (redisHandler != null) {
            redisHandler.insert(entityMap);
        }
    }

    /**
     * 在指定的redis表中删除某行记录
     *
     * @param <T>
     * @param clazz
     * @param keyValue
     */
    public <T extends Entity> void deleteRedis(Class<T> clazz, String keyValue) {
        RedisAdminContext redisAdminContext = ApplicationContext.CONTEXT.getRedisAdminContext();
        RedisHandler redisHandler = redisAdminContext.getRedisHandler(clazz);
        if (redisHandler != null) {
            redisHandler.delete(keyValue);
        }
    }
}
