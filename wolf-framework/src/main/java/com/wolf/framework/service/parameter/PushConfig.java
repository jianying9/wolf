package com.wolf.framework.service.parameter;

import com.wolf.framework.service.ResponseCode;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author jianying9
 */
@Target(value = {ElementType.ANNOTATION_TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface PushConfig {
    
    /**
     * 路由地址
     *
     * @return route
     */
    public String route();
    
    /**
     * 返回的参数
     *
     * @return String[]
     */
    public ResponseConfig[] responseConfigs() default {};
    
    /**
     * 返回标志描述
     *
     * @return
     */
    public ResponseCode[] responseCodes() default {};
}