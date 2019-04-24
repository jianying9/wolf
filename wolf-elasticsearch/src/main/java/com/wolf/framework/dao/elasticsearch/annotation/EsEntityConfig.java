package com.wolf.framework.dao.elasticsearch.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * elasticsearch entity annotation
 *
 * @author jianying9
 */
@Target(value = {ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface EsEntityConfig {

    /**
     * 表
     *
     * @return
     */
    public String table();

    /**
     * 缓存
     *
     * @return
     */
    public boolean cache() default false;

}
