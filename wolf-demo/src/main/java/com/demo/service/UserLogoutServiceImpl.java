package com.demo.service;

import com.wolf.framework.data.DataType;
import com.wolf.framework.service.ResponseState;
import com.wolf.framework.service.Service;
import com.wolf.framework.service.ServiceConfig;
import com.wolf.framework.service.parameter.ResponseConfig;
import com.wolf.framework.worker.context.MessageContext;

/**
 *
 * @author jianying9
 */
@ServiceConfig(
        desc = "用户登出",
        group = "用户",
        route = "/user/logout",
        validateSession = true,
        validateSecurity = false,
        responseConfigs = {
            @ResponseConfig(name = "userName", dataType = DataType.CHAR, desc = "帐号")
        },
        responseStates = {
            @ResponseState(state = "SUCCESS", desc = "登录成功"),
            @ResponseState(state = "FAILURE", desc = "登录失败，帐号或者密码错误")
        }
)
public class UserLogoutServiceImpl implements Service{

    @Override
    public void execute(MessageContext messageContext) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
