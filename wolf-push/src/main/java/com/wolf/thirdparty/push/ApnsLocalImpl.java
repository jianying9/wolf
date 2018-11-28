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
    private ApnsChannel apnsChannel = null;

    private String apnsFileName = "";
    private String apnsPassword = "";

    @Override
    public void init() {
        String value = ApplicationContext.CONTEXT.getParameter(ThirdPushConfig.APNS_FILE_NAME);
        if (value != null) {
            this.apnsFileName = value;
        }
        //
        value = ApplicationContext.CONTEXT.getParameter(ThirdPushConfig.APNS_PASSWORD);
        if (value != null) {
            this.apnsPassword = value;
        }
        if (this.apnsFileName.isEmpty() == false && this.apnsPassword.isEmpty() == false) {
            ApnsGateway apnsGateway = ApnsGateway.PRODUCTION;
            String compileModel = ApplicationContext.CONTEXT.getParameter(FrameworkConfig.COMPILE_MODEL);
            if (compileModel.equals(FrameworkConfig.UNIT_TEST) == false) {
                if (compileModel.equals(FrameworkConfig.DEVELOPMENT)) {
                    apnsGateway = ApnsGateway.DEVELOPMENT;
                }
                //获取apns信息文件
                ClassLoader classLoader = getClass().getClassLoader();
                URL url = classLoader.getResource(this.apnsFileName);
                ApnsChannelFactory apnsChannelFactory = Apns4j.newChannelFactoryBuilder()
                        .keyStoreMeta(url.getPath())
                        .keyStorePwd(this.apnsPassword)
                        .apnsGateway(apnsGateway)
                        .build();
                //
                this.apnsChannel = apnsChannelFactory.newChannel();
            } else {
                System.out.println("unit模式不初始apns");
            }
            ApplicationContext.CONTEXT.addResource(this);
        }
    }

    @Override
    public void destory() {
        if (this.apnsChannel != null) {
            this.logger.info("apns channel close...");
            this.apnsChannel.close();
        }
    }

    @Override
    public void push(String deviceToken, ThirdPushMessage thirdPushMessage) {
        if (this.apnsChannel != null) {
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
            this.apnsChannel.send(deviceToken, apnsPayload);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("apns push {}:{}", deviceToken, apnsPayload.toJsonString());
            }
        } else {
            this.logger.error("apns推送配置初始化缺少appFileName和apnsPassword");
        }
    }
}
