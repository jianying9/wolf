package com.wolf.framework.websocket;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author jianying9
 */
public class WebSocketWorkerContextImplTest {
    
    public WebSocketWorkerContextImplTest() {
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
     public void test() {
         String text = "{\"route\":\"/test/service/login/v1\",\"param\":{\"name\":\"test\",\"value\":\"text\"}}";
         Pattern routePattern = Pattern.compile("(?:\"route\":\")([a-zA-Z/]+\\d)(?:\")");
         Matcher matcher = routePattern.matcher(text);
         Assert.assertTrue(matcher.find());
     }
}
