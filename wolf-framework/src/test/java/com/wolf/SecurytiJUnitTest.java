package com.wolf;

import com.wolf.framework.utils.SecurityUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author jianying9
 */
public class SecurytiJUnitTest {

    public SecurytiJUnitTest() {
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
    public void desTest() {
        String key = "4102gnab";
        byte[] keyByte = key.getBytes();
        String text = SecurityUtils.byteToHexString(keyByte);
        System.out.println(text);
        //
        boolean result = SecurityUtils.isSafeTime("e4f2b4f998a0d7ae70b285b3e8a8f63a", key);
        System.out.println(result);
        
        String time = "1404471463588";
        String s = SecurityUtils.byteToHexString(SecurityUtils.encryptByDes(time, key));
        System.out.println(s);
    }
}