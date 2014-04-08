package com.wolf.framework.service.parameter;

import com.wolf.framework.data.TypeEnum;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * entity filed annotation，用于描述entity中各个field的信息
 *
 * @author aladdin
 */
@Target(value = {ElementType.ANNOTATION_TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface InputConfig {

    public String name();

    /**
     * 数据类型
     *
     * @return
     */
    public TypeEnum typeEnum();

    /**
     * 描述
     *
     * @return
     */
    public String desc();
}
