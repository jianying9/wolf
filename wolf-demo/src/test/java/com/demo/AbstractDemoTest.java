package com.demo;

import com.wolf.framework.config.FrameworkConfig;
import com.wolf.framework.test.TestHandler;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public abstract class AbstractDemoTest {

    protected static TestHandler testHandler;

    static {
        Map<String, String> parameterMap = new HashMap<String, String>(8, 1);
        parameterMap.put(FrameworkConfig.ANNOTATION_SCAN_PACKAGES, "com.demo");
        parameterMap.put(FrameworkConfig.TASK_CORE_POOL_SIZE, "1");
        parameterMap.put(FrameworkConfig.TASK_MAX_POOL_SIZE, "2");
        //
        parameterMap.put(FrameworkConfig.CASSANDRA_CONTACT_POINT, "192.168.46.182");
        parameterMap.put(FrameworkConfig.CASSANDRA_USERNAME, "test");
        parameterMap.put(FrameworkConfig.CASSANDRA_PASSWORD, "test");
        parameterMap.put("compile.model", "DEVELOPMENT");
        testHandler = new TestHandler(parameterMap);
    }
}
