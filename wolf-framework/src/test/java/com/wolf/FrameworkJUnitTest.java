package com.wolf;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.context.ApplicationContextBuilder;
import java.util.Properties;
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
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //

    @Test
    public void hello() {
        Properties configProperties = new Properties();
        configProperties.setProperty("appPath", "/test");
        configProperties.setProperty("packageName", "com.wolf.framework.dictionary.entity");
        configProperties.setProperty("hbaseZookeeperQuorum", "192.168.19.42");
        configProperties.setProperty("fsDefaultName", "hdfs://192.168.64.50:9000/");
        configProperties.setProperty("dataBaseType", "EMBEDDED");
        configProperties.setProperty("dataBaseName", "/data/derby/team");
        ApplicationContextBuilder applicationContextBuilder = new ApplicationContextBuilder(configProperties);
        applicationContextBuilder.build();
        ApplicationContext.CONTEXT.shutdownDatabase();
    }
}
