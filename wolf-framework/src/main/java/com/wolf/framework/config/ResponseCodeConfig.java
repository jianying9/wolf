package com.wolf.framework.config;

/**
 *
 * @author aladdin
 */
public class ResponseCodeConfig {

    //成功
    public final static String SUCCESS = "200";
    //未登录
    public final static String UNLOGIN = "301";
    //没有变化
    public final static String UNMODIFYED = "304";
    //非法数据
    public final static String INVALID = "400";
    //无权限
    public final static String DENIED = "403";
    //不存在
    public final static String NOTFOUND = "404";
    //未实现
    public final static String UNSUPPORT = "501";
    //异常
    public final static String EXCEPTION = "503";
}
