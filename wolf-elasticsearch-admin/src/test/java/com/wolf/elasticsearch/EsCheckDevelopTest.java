package com.wolf.elasticsearch;

import com.wolf.framework.config.FrameworkConfig;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.dao.elasticsearch.EsAdminContextImpl;
import com.wolf.framework.dao.elasticsearch.EsConfig;
import com.wolf.framework.test.TestHandler;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author jianying9
 */
public class EsCheckDevelopTest
{

    protected static TestHandler testHandler;

    static {
        Map<String, String> parameterMap = new HashMap(8, 1);
        parameterMap.put(FrameworkConfig.BUILD_TIMESTAMP, Long.toString(System.currentTimeMillis()));
        parameterMap.put(FrameworkConfig.ANNOTATION_SCAN_PACKAGES, "com.wolf.elasticsearch");
        //
        parameterMap.put(EsConfig.ELASTICSEARCH_HOST, "106.15.34.48");
        parameterMap.put(EsConfig.ELASTICSEARCH_DATABASE, "");
        parameterMap.put(EsConfig.ELASTICSEARCH_CLUSTER_NAME, "xzlm-application");
        //
        parameterMap.put(FrameworkConfig.COMPILE_MODEL, FrameworkConfig.UNIT_TEST);
        //
        testHandler = new TestHandler(parameterMap);
    }

    public EsCheckDevelopTest()
    {
    }

    @BeforeClass
    public static void setUpClass()
    {
    }

    @AfterClass
    public static void tearDownClass()
    {
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    //
    @Test
    public void check()
    {
        EsAdminContextImpl ctx = new EsAdminContextImpl(ApplicationContext.CONTEXT);
        ctx.check(Collections.EMPTY_SET);
//
    }
}
