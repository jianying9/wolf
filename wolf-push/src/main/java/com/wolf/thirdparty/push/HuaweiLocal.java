package com.wolf.thirdparty.push;

import com.wolf.framework.local.Local;

/**
 * apns服务
 *
 * @author jianying9
 */
public interface HuaweiLocal extends Local {

    public void updateAccessToken();

    public void push(String deviceToken, ThirdPushMessage thirdPushMessage);
}
