package com.demo;

import com.wolf.framework.worker.context.Response;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 *
 * @author jianying9
 */
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

    //
    @Test
    public void test0101UserRegister() {
        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("userName", "");
        Response response = testHandler.execute("/user/register", parameterMap);
        System.out.println(response.getResponseMessage());
    }
}
