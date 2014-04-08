package com.wolf.framework.service;

import com.wolf.framework.service.parameter.InputConfig;
import com.wolf.framework.service.parameter.OutputConfig;
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
     * 服务标志
     *
     * @return ActionEnum
     */
    public String actionName();

    /**
     * 重要的参数
     *
     * @return String[]
     */
    public InputConfig[] importantParameter() default {};

    /**
     * 次要的参数
     *
     * @return String[]
     */
    public InputConfig[] minorParameter() default {};

    /**
     * 返回的参数
     *
     * @return String[]
     */
    public OutputConfig[] returnParameter() default {};

    /**
     * 事务类型 需要事务控制--true 不需要事务控制--false
     *
     * @return
     */
    public boolean requireTransaction() default false;

    /**
     * 是否验证session是否过期
     *
     * @return
     */
    public boolean validateSession() default true;

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
     * 是否响应消息
     *
     * @return
     */
    public boolean response() default true;

    /**
     * 是否广播消息
     *
     * @return
     */
    public boolean broadcast() default false;

    /**
     * 描述
     *
     * @return
     */
    public String description();

    /**
     * 接口分组
     *
     * @return
     */
    public String group() default "ungrouped";

    /**
     * 自定义返回标志
     *
     * @return
     */
    public ResponseFlag[] responseFlags() default {};
}
