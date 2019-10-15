package com.wolf.framework.service.parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author jianying9
 */
@Target(value = {ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface RequestGroupConfig {
    
    /**
     * 返回的参数
     *
     * @return String[]
     */
    public ExtendRequestConfig[] requestConfigs() default {};
    
}
