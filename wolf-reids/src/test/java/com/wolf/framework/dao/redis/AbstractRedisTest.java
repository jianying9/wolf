package com.wolf.framework.dao.redis;

import com.wolf.framework.config.FrameworkConfig;
import com.wolf.framework.test.TestHandler;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public abstract class AbstractRedisTest {

    protected static TestHandler testHandler;

    static {
        Map<String, String> parameterMap = new HashMap<String, String>(8, 1);
        parameterMap.put(FrameworkConfig.ANNOTATION_SCAN_PACKAGES, "com.wolf.framework.dao.redis");
        parameterMap.put(FrameworkConfig.TASK_CORE_POOL_SIZE, "1");
        parameterMap.put(FrameworkConfig.TASK_MAX_POOL_SIZE, "2");
        //
        parameterMap.put(FrameworkConfig.REDIS_SERVER_HOST, "192.168.19.137");
        parameterMap.put(FrameworkConfig.REDIS_SERVER_PORT, "6379");
        parameterMap.put(FrameworkConfig.REDIS_MAX_POOL_SIZE, "1");
        parameterMap.put(FrameworkConfig.REDIS_MIN_POOL_SIZE, "2");
        parameterMap.put("compile.model", "DEVELOPMENT");
        testHandler = new TestHandler(parameterMap);
    }

    public static void setReceptionSession(String userId) {
        testHandler.setSessionId(userId);
    }

    public static void setCustomerSession(String userId) {
        testHandler.setSessionId(userId);
    }
}
