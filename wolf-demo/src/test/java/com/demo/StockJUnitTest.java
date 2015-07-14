package com.demo;

import com.wolf.framework.worker.context.Response;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 *
 *
 * @author jianying9
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StockJUnitTest extends AbstractDemoTest{

    public StockJUnitTest() {
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
    
    private static String sid;

    //
//    @Test
    public void test0101stockInsert() {
        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("id", "600649");
        Response response = testHandler.execute("/stock/insert", parameterMap);
        System.out.println(response.getResponseMessage());
    }
    
//    @Test
    public void test0201stockMoneyFlowUpdate() {
        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("id", "600649");
        Response response = testHandler.execute("/stock/moneyflow/update", parameterMap);
        System.out.println(response.getResponseMessage());
    }
    
//    @Test
    public void test0301UpdateStockMoneyFlowMinute() {
        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("type", "minute");
        Response response = testHandler.execute("/stock/moneyflow/timer/update", parameterMap);
        System.out.println(response.getResponseMessage());
    }
    
//    @Test
    public void test0401TruncateStockMoneyFlowMinute() {
        Map<String, String> parameterMap = new HashMap<String, String>();
        Response response = testHandler.execute("/stock/moneyflow/minute/timer/truncate", parameterMap);
        System.out.println(response.getResponseMessage());
    }
    
    @Test
    public void test0501UpdateStockMoneyFlowDay() {
        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("type", "day");
        Response response = testHandler.execute("/stock/moneyflow/timer/update", parameterMap);
        System.out.println(response.getResponseMessage());
    }
}
