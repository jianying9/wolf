package com.wolf.thirdparty.push;

/**
 *
 * @author jianying9
 */
public class HuaweiChannel {

    private final String name;
    private final String appId;
    private final String appSecret;
    private final String packageName;
    private String accessToken = "";
    private long tokenExpiredTime = 0;

    public HuaweiChannel(String name, String appId, String appSecret, String packageName) {
        this.name = name;
        this.appId = appId;
        this.appSecret = appSecret;
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public String getAppId() {
        return appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public long getTokenExpiredTime() {
        return tokenExpiredTime;
    }

    public void update(String accessToken, long tokenExpiredTime) {
        this.accessToken = accessToken;
        this.tokenExpiredTime = tokenExpiredTime;
    }

}
