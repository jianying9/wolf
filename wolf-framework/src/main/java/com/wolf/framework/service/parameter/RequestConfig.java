package com.wolf.framework.service.parameter;

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
public @interface RequestConfig {

    /**
     * 参数名
     * @return 
     */
    public String name();

    /**
     * 是否必填,默认ture
     * @return 
     */
    public boolean required() default true;

    /**
     * 数据类型
     *
     * @return
     */
    public RequestDataType dataType();
    
    /**
     * 最大
     * @return 
     */
    public long max() default Long.MAX_VALUE;
    
    /**
     * 最小
     * @return 
     */
    public long min() default 0;
    
    /**
     * 自定义文本
     * @return 
     */
    public String text() default "0|1";

    /**
     * 描述
     *
     * @return
     */
    public String desc();
}
