package com.wolf.thirdparty.push;

import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.context.Resource;
import com.wolf.framework.local.LocalServiceConfig;
import com.wolf.framework.logger.LogFactory;
import com.xiaomi.xmpush.server.Constants;
import com.xiaomi.xmpush.server.Message;
import com.xiaomi.xmpush.server.Result;
import com.xiaomi.xmpush.server.Sender;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;

/**
 *
 * @author jianying9
 */
@LocalServiceConfig
public class XiaomiLocalImpl implements XiaomiLocal, Resource {

    private String defaultChannelName = "defaultChannel";

    private final Map<String, XiaomiChannel> channelMap = new HashMap(4, 1);

    private final Logger logger = LogFactory.getLogger(FrameworkLogger.XIAOMI);

    @Override
    public void init() {
        String appSecret = "";
        String value = ApplicationContext.CONTEXT.getParameter(ThirdPushConfig.XIAOMI_APP_SECRET);
        if (value != null) {
            appSecret = value;
        }
        //
        String packageName = "";
        value = ApplicationContext.CONTEXT.getParameter(ThirdPushConfig.XIAOMI_PACKAGE_NAME);
        if (value != null) {
            packageName = value;
        }
        //
        value = ApplicationContext.CONTEXT.getParameter(ThirdPushConfig.DEFAULT_CHANNEL_NAME);
        if (value != null) {
            defaultChannelName = value;
        }
        //
        if (appSecret.isEmpty() == false && packageName.isEmpty() == false) {
            XiaomiChannel xiaomiChannel = new XiaomiChannel(this.defaultChannelName, appSecret, packageName);
            this.channelMap.put(this.defaultChannelName, xiaomiChannel);
        }
        //启用小米正式环境推送
        Constants.useOfficial();
    }

    @Override
    public void destory() {
    }

    private String getStringValue(Object value) {
        String result = "";
        if (String.class.isInstance(value)) {
            result = (String) value;
        } else if (Long.class.isInstance(value)) {
            result = Long.toString((Long) value);
        } else if (Integer.class.isInstance(value)) {
            result = Integer.toString((Integer) value);
        } else if (Boolean.class.isInstance(value)) {
            result = Boolean.toString((Boolean) value);
        } else if (Double.class.isInstance(value)) {
            result = Double.toString((Double) value);
        }
        return result;
    }

    @Override
    public void add(XiaomiChannel xiaomiChannel) {
        this.channelMap.put(xiaomiChannel.getName(), xiaomiChannel);
    }

    @Override
    public void push(String deviceToken, ThirdPushMessage thirdPushMessage) {
        this.push(defaultChannelName, deviceToken, thirdPushMessage);
    }

    @Override
    public void push(String channelName, String deviceToken, ThirdPushMessage thirdPushMessage) {
        if (thirdPushMessage.isValid()) {
            XiaomiChannel xiaomiChannel = this.channelMap.get(channelName);
            if (xiaomiChannel != null) {
                Message.Builder builder = new Message.Builder();
                String title = thirdPushMessage.getTitle();
                String content = thirdPushMessage.getContent();
                builder.title(title).description(content);
                //扩展参数
                Map<String, Object> extendMap = thirdPushMessage.getExtendMap();
                Set<Map.Entry<String, Object>> entrySet = extendMap.entrySet();
                String value;
                for (Map.Entry<String, Object> entry : entrySet) {
                    value = this.getStringValue(entry.getValue());
                    builder.extra(entry.getKey(), value);
                }
                //
                int notifyType = -1;

                if (thirdPushMessage.isSound()) {
                    notifyType = 1;
                }
                builder.notifyType(notifyType);
                //
                //1表示透传消息, 0表示通知栏消息
                int passThrough = 0;
                builder.passThrough(passThrough);
                //通知分组id
                builder.notifyId(thirdPushMessage.getNotifyId());
                //包名
                builder.restrictedPackageName(xiaomiChannel.getPackageName());
                //
                Message message = builder.build();
                Sender sender = new Sender(xiaomiChannel.getAppSecret());
                try {
                    Result result;
                    if (deviceToken.length() > 13) {
                        //使用regid推送
                        result = sender.send(message, deviceToken, 0);
                    } else {
                        //使用别名推送
                        result = sender.sendToAlias(message, deviceToken, 0);
                    }
                    this.logger.debug("xiaomi push success,MessageId:{},ErrorCode:{},Reason:{}", result.getMessageId(), result.getErrorCode(), result.getReason());
                } catch (IOException | ParseException ex) {
                    this.logger.error("xiaomi push exception", ex);
                }
            } else {
                this.logger.error("xiaomi推送配置初始化缺少appSecret和packageName");
            }
        }
    }
}
