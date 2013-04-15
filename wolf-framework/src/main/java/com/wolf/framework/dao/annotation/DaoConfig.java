package com.wolf.framework.dao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * entity annotation，用于描述entity的信息
 *
 * @author aladdin
 */
@Target(value = {ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface DaoConfig {

    /**
     * 实体标识
     *
     * @return
     */
    String tableName();

    /**
     * 是否启用缓存
     *
     * @return
     */
    boolean useCache() default false;

    /**
     * 缓存最大数量
     *
     * @return
     */
    int maxEntriesLocalHeap() default 1000;

    /**
     * 最长闲置时间
     *
     * @return
     */
    int timeToIdleSeconds() default 300;

    /**
     * 最长存活时间
     *
     * @return
     */
    int timeToLiveSeconds() default 3600;
}
