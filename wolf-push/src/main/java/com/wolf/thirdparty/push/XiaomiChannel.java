package com.wolf.thirdparty.push;

/**
 *
 * @author jianying9
 */
public class XiaomiChannel {

    private final String name;
    private final String appSecret;
    private final String packageName;

    public XiaomiChannel(String name, String appSecret, String packageName) {
        this.name = name;
        this.appSecret = appSecret;
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public String getPackageName() {
        return packageName;
    }

}
