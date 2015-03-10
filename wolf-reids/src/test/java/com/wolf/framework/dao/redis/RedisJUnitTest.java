package com.wolf.framework.dao.redis;

import com.wolf.framework.dao.reids.TestRedisHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * Created by lgf on 2014/9/11.
 */
public class RedisJUnitTest extends AbstractRedisTest {

    public RedisJUnitTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void test01() {
        TestRedisHandler.deleteRedis(TestRedisEntity.class, "1");
    }
}
