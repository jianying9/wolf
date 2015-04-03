package com.demo.service;

import com.wolf.framework.data.DataType;
import com.wolf.framework.service.ResponseState;
import com.wolf.framework.service.Service;
import com.wolf.framework.service.ServiceConfig;
import com.wolf.framework.service.parameter.RequestConfig;
import com.wolf.framework.service.parameter.ResponseConfig;
import com.wolf.framework.worker.context.MessageContext;

/**
 *
 * @author jianying9
 */
@ServiceConfig(
        desc = "新用户注册",
        group = "用户",
        route = "/user/register",
        validateSession = false,
        validateSecurity = false,
        requestConfigs = {
            @RequestConfig(name = "userName", must = true, dataType = DataType.CHAR, max = 64, min = 6, desc = "帐号"),
            @RequestConfig(name = "password", must = true, dataType = DataType.CHAR, max = 36,desc = "md5加密后密码")
        },
        responseConfigs = {
            @ResponseConfig(name = "userName", dataType = DataType.CHAR, desc = "帐号")
        },
        responseStates = {
            @ResponseState(state = "SUCCESS", desc = "注册成功"),
            @ResponseState(state = "FAILURE", desc = "注册失败，帐号已经被使用")
        }
)
public class UserRegisterServiceImpl implements Service{

    @Override
    public void execute(MessageContext messageContext) {
        
    }
}
