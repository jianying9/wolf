package com.wolf.thirdparty.push;

import cn.teaey.apns4j.Apns4j;
import cn.teaey.apns4j.network.ApnsChannel;
import cn.teaey.apns4j.network.ApnsChannelFactory;
import cn.teaey.apns4j.network.ApnsGateway;
import cn.teaey.apns4j.protocol.ApnsPayload;
import com.wolf.framework.config.FrameworkConfig;
import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.context.Resource;
import com.wolf.framework.local.LocalServiceConfig;
import com.wolf.framework.logger.LogFactory;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;

/**
 *
 * @author jianying9
 */
@LocalServiceConfig
public class ApnsLocalImpl implements ApnsLocal, Resource {

    private final Logger logger = LogFactory.getLogger(FrameworkLogger.APNS);
    private String defaultChannelName = "defaultChannel";
    private final Map<String, ApnsChannel> channelMap = new HashMap(4, 1);

    @Override
    public void init() {
        String apnsFileName = "";
        String value = ApplicationContext.CONTEXT.getParameter(ThirdPushConfig.APNS_FILE_NAME);
        if (value != null) {
            apnsFileName = value;
        }
        //
        String apnsPassword = "";
        value = ApplicationContext.CONTEXT.getParameter(ThirdPushConfig.APNS_PASSWORD);
        if (value != null) {
            apnsPassword = value;
        }
        //
        value = ApplicationContext.CONTEXT.getParameter(ThirdPushConfig.DEFAULT_CHANNEL_NAME);
        if (value != null) {
            defaultChannelName = value;
        }
        if (apnsFileName.isEmpty() == false && apnsPassword.isEmpty() == false) {
            ApnsGateway apnsGateway = ApnsGateway.PRODUCTION;
            String compileModel = ApplicationContext.CONTEXT.getParameter(FrameworkConfig.COMPILE_MODEL);
            if (compileModel.equals(FrameworkConfig.UNIT_TEST) == false) {
                if (compileModel.equals(FrameworkConfig.DEVELOPMENT)) {
                    apnsGateway = ApnsGateway.DEVELOPMENT;
                }
                //获取apns信息文件
                ClassLoader classLoader = getClass().getClassLoader();
                URL url = classLoader.getResource(apnsFileName);
                ApnsChannelFactory apnsChannelFactory = Apns4j.newChannelFactoryBuilder()
                        .keyStoreMeta(url.getPath())
                        .keyStorePwd(apnsPassword)
                        .apnsGateway(apnsGateway)
                        .build();
                //
                ApnsChannel defaultChannel = apnsChannelFactory.newChannel();
                this.channelMap.put(this.defaultChannelName, defaultChannel);
            } else {
                System.out.println("unit模式不初始apns");
            }
            ApplicationContext.CONTEXT.addResource(this);
        }
    }

    @Override
    public void destory() {
        if (this.channelMap.isEmpty() == false) {
            this.logger.info("apns channel close...");
            for (ApnsChannel apnsChannel : this.channelMap.values()) {
                apnsChannel.close();
            }
        }
    }

    @Override
    public void add(String channelName, String apnsSecretPath, String apnsSecretPassword) {
        if (apnsSecretPath.isEmpty() == false && apnsSecretPassword.isEmpty() == false && this.channelMap.containsKey(channelName) == false) {
            //获取apns信息文件
            ApnsGateway apnsGateway = ApnsGateway.PRODUCTION;
            String compileModel = ApplicationContext.CONTEXT.getParameter(FrameworkConfig.COMPILE_MODEL);
            if (compileModel.equals(FrameworkConfig.DEVELOPMENT)) {
                apnsGateway = ApnsGateway.DEVELOPMENT;
            }
            ApnsChannelFactory apnsChannelFactory = Apns4j.newChannelFactoryBuilder()
                    .keyStoreMeta(apnsSecretPath)
                    .keyStorePwd(apnsSecretPassword)
                    .apnsGateway(apnsGateway)
                    .build();
            //
            ApnsChannel apnsChannel = apnsChannelFactory.newChannel();
            this.channelMap.put(channelName, apnsChannel);
        }
    }

    @Override
    public void push(String deviceToken, ThirdPushMessage thirdPushMessage) {
        this.push(defaultChannelName, deviceToken, thirdPushMessage);
    }

    @Override
    public void push(String channelName, String deviceToken, ThirdPushMessage thirdPushMessage) {
        if (thirdPushMessage.isValid()) {
            ApnsChannel apnsChannel = this.channelMap.get(channelName);
            if (apnsChannel != null) {
                //
                ApnsPayload apnsPayload = Apns4j.newPayload();
                String title = thirdPushMessage.getTitle();
                String content = thirdPushMessage.getContent();
                apnsPayload.alertTitle(title).alertBody(content);
                //扩展参数
                Map<String, Object> extendMap = thirdPushMessage.getExtendMap();
                Set<Map.Entry<String, Object>> entrySet = extendMap.entrySet();
                for (Map.Entry<String, Object> entry : entrySet) {
                    apnsPayload.extend(entry.getKey(), entry.getValue());
                }
                //
                Long count = thirdPushMessage.getCount();
                if (count != null) {
                    apnsPayload.badge(count.intValue());
                }
                //
                if (thirdPushMessage.isSound()) {
                    apnsPayload.sound("default");
                } else {
                    apnsPayload.silent();
                }
                apnsChannel.send(deviceToken, apnsPayload);
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("apns push {}:{}", deviceToken, apnsPayload.toJsonString());
                }
            } else {
                this.logger.error("apns推送" + channelName + "配置初始化缺少appFileName和apnsPassword");
            }
        }
    }
}
