package com.wolf.thirdparty.push;

import com.wolf.framework.local.Local;

/**
 * apns服务
 *
 * @author jianying9
 */
public interface XiaomiLocal extends Local {

    public void push(String deviceToken, ThirdPushMessage thirdPushMessage);
}
