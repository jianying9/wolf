package com.demo.service;

import com.demo.entity.UserEntity;
import com.demo.localservice.UserLocalService;
import com.wolf.framework.data.DataType;
import com.wolf.framework.local.InjectLocalService;
import com.wolf.framework.service.ResponseState;
import com.wolf.framework.service.Service;
import com.wolf.framework.service.ServiceConfig;
import com.wolf.framework.service.SessionHandleType;
import com.wolf.framework.service.parameter.RequestConfig;
import com.wolf.framework.service.parameter.ResponseConfig;
import com.wolf.framework.worker.context.MessageContext;
import java.util.UUID;

/**
 *
 * @author jianying9
 */
@ServiceConfig(
        desc = "用户登录",
        group = "用户",
        route = "/user/login",
        validateSession = false,
        validateSecurity = false,
        sessionHandleType = SessionHandleType.SAVE,
        requestConfigs = {
            @RequestConfig(name = "userName", must = true, dataType = DataType.CHAR, max = 64, min = 6, desc = "帐号"),
            @RequestConfig(name = "password", must = true, dataType = DataType.CHAR, max = 36,desc = "md5加密后密码")
        },
        responseConfigs = {
            @ResponseConfig(name = "userName", dataType = DataType.CHAR, desc = "帐号")
        },
        responseStates = {
            @ResponseState(state = "SUCCESS", desc = "登录成功"),
            @ResponseState(state = "FAILURE", desc = "登录失败，帐号或者密码错误")
        }
)
public class UserLoginServiceImpl implements Service{
    
    @InjectLocalService()
    private UserLocalService userLocalService;

    @Override
    public void execute(MessageContext messageContext) {
        String userName = messageContext.getParameter("userName");
        UserEntity userEntity = this.userLocalService.inquireByUserName(userName);
        if(userEntity != null) {
            //帐号存在
            String password = messageContext.getParameter("password");
            if(userEntity.getPassword().equals(password)) {
                //session
                String sid = UUID.randomUUID().toString();
                this.userLocalService.insertSession(userName, sid);
                //密码正确,记录登录次数加1
                this.userLocalService.countLogin(userName);
                messageContext.setNewSessionId(sid);
                messageContext.success();
            }
        }
    }
}
