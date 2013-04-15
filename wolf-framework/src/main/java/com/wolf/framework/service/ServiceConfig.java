package com.wolf.framework.service;

import com.wolf.framework.service.parameter.Parameter;
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
    public String[] importantParameter() default {};

    /**
     * 次要的参数
     *
     * @return String[]
     */
    public String[] minorParameter() default {};

    /**
     * 次要参数处理方法 KEEP_EMPEY-保留空字符串 DISCARD_EMPTY-丢弃空字符串
     * DEFAULT_REPLACE_NULL-用缺省值填充NULL
     *
     * @return
     */
    public MinorHandlerTypeEnum minorHandlerTypeEnum() default MinorHandlerTypeEnum.KEEP_EMPTY;

    /**
     * 返回的参数
     *
     * @return String[]
     */
    public String[] returnParameter() default {};

    /**
     * 验证参数来源，实体有顺序，如果有重复取最先出现的ExtendedEntityEnum中的field信息
     *
     * @return
     */
    public Class<? extends Parameter>[] parametersConfigs() default {};

    /**
     * 多参数单值,用Map<String,String>来传递---SIMPLE_MAP 不获取任何参数---NO_PARAMETER
     *
     * @return
     */
    public ParameterTypeEnum parameterTypeEnum() default ParameterTypeEnum.NO_PARAMETER;

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
     * @return 
     */
    public boolean page() default false;

    /**
     * 是否响应消息
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
}
