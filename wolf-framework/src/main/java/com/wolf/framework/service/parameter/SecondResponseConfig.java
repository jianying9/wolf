package com.wolf.framework.service.parameter;

import com.wolf.framework.service.parameter.filter.FilterType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author aladdin
 */
@Target(value = {ElementType.ANNOTATION_TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface SecondResponseConfig {

    /**
     * 参数名
     * @return 
     */
    public String name();

    /**
     * 数据类型
     *
     * @return
     */
    public ResponseDataType dataType();
    
    /**
     * 描述
     *
     * @return
     */
    public String desc();

    /**
     * String类型在输出时过滤行为
     *
     * @return
     */
    public FilterType[] filterTypes() default {FilterType.SECURITY};
    
    /**
     * 三级参数配置
     * @return 
     */
    public ThirdResponseConfig[] thirdResponseConfigs() default {};
}
