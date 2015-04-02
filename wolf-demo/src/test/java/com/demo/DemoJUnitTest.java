package com.demo;

import java.util.regex.Pattern;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 *
 * @author jianying9
 */
public class DemoJUnitTest {

    public DemoJUnitTest() {
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

    //
    @Test
    public void hello() {
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
//    login counter,
//    message counter
//);
//
//CREATE TABLE test.user (
//    user_name text PRIMARY KEY,
//    create_time bigint,
//    friend set<text>,
//    message list<text>,
//    other_id map<text, text>,
//    password text,
//    tag set<text>
//);
//CREATE TABLE test.table_count (
//    table_name text PRIMARY KEY,
//    "count" counter
//);
    }
}
