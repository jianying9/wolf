package com.wolf;

import com.wolf.framework.config.FrameworkConfig;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.context.ApplicationContextBuilder;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author aladdin
 */
public class FrameworkJUnitTest {

    public FrameworkJUnitTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void hello() {
        Map<String, String> parameterMap = new HashMap<String, String>(8, 1);
        parameterMap.put(FrameworkConfig.COMPILE_MODEL, FrameworkConfig.DEVELOPMENT);
        parameterMap.put(FrameworkConfig.ANNOTATION_SCAN_PACKAGES, "com.wolf");
        parameterMap.put(FrameworkConfig.TASK_CORE_POOL_SIZE, "10");
        parameterMap.put(FrameworkConfig.TASK_MAX_POOL_SIZE, "20");
        //
        parameterMap.put(FrameworkConfig.REDIS_SERVER_HOST, "192.168.59.49");
        parameterMap.put(FrameworkConfig.REDIS_SERVER_PORT, "6379");
        parameterMap.put(FrameworkConfig.REDIS_MAX_POOL_SIZE, "20");
        parameterMap.put(FrameworkConfig.REDIS_MIN_POOL_SIZE, "10");
        ApplicationContextBuilder applicationContextBuilder = new ApplicationContextBuilder(parameterMap);
        applicationContextBuilder.build();
        ApplicationContext.CONTEXT.contextDestroyed();
    }
}
