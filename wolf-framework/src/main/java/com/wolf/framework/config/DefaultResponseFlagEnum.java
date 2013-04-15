package com.wolf.framework.config;

/**
 *
 * @author aladdin
 */
public enum DefaultResponseFlagEnum implements ResponseFlagType {

    //成功
    SUCCESS,
    //失败
    FAILURE,
    //未登录
    UNLOGIN,
    //非法数据
    INVALID,
    //无权限
    DENIED,
    //超时
    TIMEOUT,
    //异常
    EXCEPTION;

    @Override
    public String getFlagName() {
        return this.name();
    }
}
