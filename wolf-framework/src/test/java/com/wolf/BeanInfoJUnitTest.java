package com.wolf;

import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author jianying9
 */
public class BeanInfoJUnitTest {
    
    public BeanInfoJUnitTest() {
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
    
//java.lang.String
//long
//java.lang.Long
//int
//java.lang.Integer
//boolean
//java.lang.Boolean
    
    private void read(String... values) {
        System.out.println(values.length);
        System.out.println(values.getClass());
    }

    //
     @Test
     public void hello() throws IntrospectionException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
         TestEntity te = new TestEntity();
         Field[] fields = TestEntity.class.getDeclaredFields();
         for (Field field : fields) {
             Class<?> clazz = field.getType();
             System.out.println(clazz.getName());
         }
         Field field = TestEntity.class.getDeclaredField("name");
         field.setAccessible(true);
         field.set(te, "a");
         System.out.println(te.getName());
         this.read("a");
         this.read("a", "b");
         String[] as = {"a", "b", "c"};
         this.read(as);
     }
}
