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
}
