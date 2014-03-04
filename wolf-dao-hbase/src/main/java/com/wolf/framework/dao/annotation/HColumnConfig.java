package com.wolf.framework.dao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author aladdin
 */
@Target(value = {ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface HColumnConfig {

    /**
     * 是否主键
     * 
     * @return
     */
    public boolean key() default false;

    /**
     * 描述
     *
     * @return
     */
    public String desc();
}
