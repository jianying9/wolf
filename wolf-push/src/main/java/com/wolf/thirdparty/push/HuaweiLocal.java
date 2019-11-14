package com.wolf.thirdparty.push;

import com.wolf.framework.local.Local;

/**
 * apns服务
 *
 * @author jianying9
 */
public interface HuaweiLocal extends Local {

    public void add(HuaweiChannel huaweiChannel);

    public void push(String deviceToken, ThirdPushMessage thirdPushMessage);

    public void push(String channelName, String deviceToken, ThirdPushMessage thirdPushMessage);
}
