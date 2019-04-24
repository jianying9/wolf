package com.wolf.framework.dao.elasticsearch.annotation;

import com.wolf.framework.dao.Entity;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author jianying9
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InjectEsEntityDao {

    public Class<? extends Entity> clazz();
}
