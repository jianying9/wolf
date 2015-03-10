package com.wolf.framework.service;

import com.wolf.framework.service.parameter.RequestConfig;
import com.wolf.framework.service.parameter.ResponseConfig;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务信息配置
 *
 * @author aladdin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ServiceConfig {

    /**
     * 路由地址
     *
     * @return route
     */
    public String route();

    /**
     * 重要的参数
     *
     * @return String[]
     */
    public RequestConfig[] requestConfigs() default {};

    /**
     * 返回的参数
     *
     * @return String[]
     */
    public ResponseConfig[] responseConfigs() default {};

    /**
     * 事务类型 需要事务控制--true 不需要事务控制--false
     *
     * @return
     */
    public boolean requireTransaction() default false;

    /**
     * 是否验证session
     *
     * @return
     */
    public boolean validateSession() default true;
    /**
     * 是否验证访问来至合法的客户端
     *
     * @return
     */
    public boolean validateSecurity() default true;

    /**
     * 是否设置session(用于保持连接)
     *
     * @return
     */
    public SessionHandleTypeEnum sessionHandleTypeEnum() default SessionHandleTypeEnum.NONE;

    /**
     * 是否分页
     *
     * @return
     */
    public boolean page() default false;

    /**
     * 描述
     *
     * @return
     */
    public String desc();

    /**
     * 接口分组
     *
     * @return
     */
    public String group() default "default";

    /**
     * 返回标志描述
     *
     * @return
     */
    public ResponseState[] responseStates();
}
