package com.wolf.framework.dao.reids.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * redis entity annotation
 *
 * @author aladdin
 */
@Target(value = {ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface RDaoConfig{

    /**
     * 实体标识
     *
     * @return
     */
    public String tableName();
    
    /**
     * 可以排序的set集合定义
     * @return 
     */
    public String[] sortedSets() default {};
}
