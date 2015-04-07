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
public class DemoJUnitTest extends AbstractDemoTest{

    public DemoJUnitTest() {
    }
//cassandra cql:    
//CREATE KEYSPACE test WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '2'}  AND durable_writes = true;
//
//CREATE TABLE test.session (
//    id text PRIMARY KEY,
//    create_time bigint,
//    user_name text
//);
//CREATE INDEX index_session_user_name ON test.session (user_name);
//
//CREATE TABLE test.user_count (
//    user_name text PRIMARY KEY,
//    login counter
//);
//
//CREATE TABLE test.user (
//    user_name text PRIMARY KEY,
//    create_time bigint,
//    password text,
//    tag set<text>
//);
//CREATE TABLE test.table_count (
//    table_name text PRIMARY KEY,
//    "count" counter
//);

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
    @Test
    public void test0101UserRegister() {
        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("userName", "test01");
        parameterMap.put("password", "111111");
        Response response = testHandler.execute("/user/register", parameterMap);
        System.out.println(response.getResponseMessage());
    }
    
    @Test
    public void test0103UserLogin() {
        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("userName", "test01");
        parameterMap.put("password", "111111");
        Response response = testHandler.execute("/user/login", parameterMap);
        sid = response.getSessionId();
        System.out.println(response.getResponseMessage());
    }
    
    @Test
    public void test0105UserLogout() {
        testHandler.setSessionId(sid);
        Map<String, String> parameterMap = new HashMap<String, String>();
        Response response = testHandler.execute("/user/logout", parameterMap);
        System.out.println(response.getResponseMessage());
    }
}
