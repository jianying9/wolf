package com.wolf.framework.websocket;

import com.wolf.framework.worker.ServiceWorker;
import com.wolf.framework.worker.context.WebSocketWorkerContextImpl;
import com.wolf.framework.worker.context.WorkerContext;
import javax.websocket.Session;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

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

//     @Test
     public void test() {
         SessionManager sessionManager = null;
         Session session = null;
         String route = "/test";
         String text = "{\"route\":\"/test\",\"param\":{\"name\":\"test\",\"value\":\"text\"}}";
         ServiceWorker serviceWorker = null;
         WorkerContext workerContext = new WebSocketWorkerContextImpl(sessionManager, session, route, text, serviceWorker);
     }
}
