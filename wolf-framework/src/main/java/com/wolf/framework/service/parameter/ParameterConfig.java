package com.wolf.framework.service.parameter;

import com.wolf.framework.data.DataTypeEnum;
import com.wolf.framework.service.parameter.filter.FilterTypeEnum;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * entity filed annotation，用于描述entity中各个field的信息
 *
 * @author aladdin
 */
@Target(value = {ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ParameterConfig {
    
    public ParameterTypeEnum parameterTypeEnum() default ParameterTypeEnum.BASIC;

    /**
     * 数据类型
     *
     * @return
     */
    public DataTypeEnum dateTypeEnum() default DataTypeEnum.CHAR_60;

    /**
     * 描述
     *
     * @return
     */
    public String desc();

    /**
     * 该parameter在输出时过滤行为
     *
     * @return
     */
    public FilterTypeEnum[] filterTypes() default {FilterTypeEnum.ESCAPE, FilterTypeEnum.SECURITY};

    /**
     * 自定义默认值，将会覆盖字段类型的默认值
     */
    public String defaultValue() default "";
}
