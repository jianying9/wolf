package com.wolf.framework.dao.cassandra.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * cassandra entity annotation
 *
 * @author jianying9
 */
@Target(value = {ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface CDaoConfig{
    
    /**
     * 表空间
     * @return 
     */
    public String keyspace();

    /**
     * 表
     *
     * @return
     */
    public String table();
    
    /**
     * 是否是计数表
     * @return 
     */
    public boolean counter() default false;
    
    /**
     * set类型列定义
     * @return 
     */
//    public String[] sets() default {};
    
    /**
     * list类型列定义
     * @return 
     */
//    public String[] lists() default {};
    
    /**
     * map类型列定义
     * @return 
     */
//    public String[] maps() default {};
}
