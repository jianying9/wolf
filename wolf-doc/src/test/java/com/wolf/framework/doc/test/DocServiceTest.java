package com.wolf.framework.doc.test;

import com.wolf.framework.reponse.Response;
import com.wolf.framework.test.TestHandler;
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
public class DocServiceTest {
    
    public DocServiceTest() {
    }
    
    private static TestHandler testHander = null;
    
    @BeforeClass
    public static void setUpClass() {
        Map<String, String> parameterMap = new HashMap<>();
        testHander = new TestHandler(parameterMap);
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
     public void testGroup() {
         Response response = testHander.execute("/wolf/group", new HashMap<String, String>());
         System.out.println(response.getResponseMessage());
     }
     
     @Test
     public void testService() {
         Response response = testHander.execute("/wolf/service", new HashMap<String, String>());
         System.out.println(response.getResponseMessage());
     }
     
     @Test
     public void testRoute() {
         Map<String, String> parameterMap = new HashMap<>();
         parameterMap.put("routeName", "/doc/test/list");
         Response response = testHander.execute("/wolf/service/info", parameterMap);
         System.out.println(response.getResponseMessage());
     }
}
