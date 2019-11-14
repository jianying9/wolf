package com.wolf.thirdparty.push;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.context.Resource;
import com.wolf.framework.local.InjectLocalService;
import com.wolf.framework.local.LocalServiceConfig;
import com.wolf.framework.logger.LogFactory;
import com.wolf.thirdparty.http.HttpLocal;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;

/**
 *
 * @author jianying9
 */
@LocalServiceConfig
public class HuaweiLocalImpl implements HuaweiLocal, Resource {

    private final Logger logger = LogFactory.getLogger(FrameworkLogger.XIAOMI);

    private String defaultChannelName = "defaultChannel";

    private final String tokenUrl = "https://login.cloud.huawei.com/oauth2/v2/token"; //获取认证Token的URL

    private final String apiUrl = "https://api.push.hicloud.com/pushsend.do"; //应用级消息下发API 

    private final Map<String, HuaweiChannel> channelMap = new HashMap(4, 1);

    //
    @InjectLocalService
    private HttpLocal httpLocal;

    public void setHttpLocal(HttpLocal httpLocal) {
        this.httpLocal = httpLocal;
    }

    public void addChannel(String name, HuaweiChannel huaweiChannel) {
        this.channelMap.put(name, huaweiChannel);
    }

    @Override
    public void init() {
        String appId = "";
        String value = ApplicationContext.CONTEXT.getParameter(ThirdPushConfig.HUAWEI_APP_ID);
        if (value != null) {
            appId = value;
        }
        //
        String appSecret = "";
        value = ApplicationContext.CONTEXT.getParameter(ThirdPushConfig.HUAWEI_APP_SECRET);
        if (value != null) {
            appSecret = value;
        }
        //
        String packageName = "";
        value = ApplicationContext.CONTEXT.getParameter(ThirdPushConfig.HUAWEI_PACKAGE_NAME);
        if (value != null) {
            packageName = value;
        }
        //
        value = ApplicationContext.CONTEXT.getParameter(ThirdPushConfig.DEFAULT_CHANNEL_NAME);
        if (value != null) {
            defaultChannelName = value;
        }
        //
        if (appId.isEmpty() == false && appSecret.isEmpty() == false && packageName.isEmpty() == false) {
            HuaweiChannel huaweiChannel = new HuaweiChannel(this.defaultChannelName, appId, appSecret, packageName);
            this.channelMap.put(this.defaultChannelName, huaweiChannel);
        }
    }

    @Override
    public void destory() {
    }

    private synchronized void updateAccessToken(String channelName) {
        HuaweiChannel huaweiChannel = this.channelMap.get(channelName);
        if (huaweiChannel != null) {
            //获取token
            Map<String, String> paramterMap = new HashMap(4, 1);
            paramterMap.put("grant_type", "client_credentials");
            paramterMap.put("client_secret", huaweiChannel.getAppSecret());
            paramterMap.put("client_id", huaweiChannel.getAppId());
            //
            Map<String, String> headerMap = new HashMap(2, 1);
            headerMap.put("Content-Type", "application/x-www-form-urlencoded");
            String json = this.httpLocal.doPost(this.tokenUrl, paramterMap, headerMap);
            //处理返回结果
            JsonNode rootNode = null;
            if (json != null && json.isEmpty() == false) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    rootNode = mapper.readValue(json, JsonNode.class);
                } catch (IOException e) {
                    this.logger.error("huawei update accesstoken response error:{}", json);
                }
            }
            //
            if (rootNode != null) {
                JsonNode accessTokenNode = rootNode.get("access_token");
                if (accessTokenNode != null) {
                    String accessToken = accessTokenNode.asText();
                    //
                    JsonNode expiresInNode = rootNode.get("expires_in");
                    long expiresIn = expiresInNode.asLong();
                    long tokenExpiredTime = System.currentTimeMillis() + expiresIn * 900l;
                    huaweiChannel.update(accessToken, tokenExpiredTime);
                    this.logger.info("huawei update accesstoken success:{},{}", channelName, accessToken);
                } else {
                    this.logger.error("huawei update accesstoken error:{}", json);
                }
            }
        }
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
    public void add(HuaweiChannel huaweiChannel) {
        this.channelMap.put(huaweiChannel.getName(), huaweiChannel);
    }

    @Override
    public void push(String deviceToken, ThirdPushMessage thirdPushMessage) {
        this.push(this.defaultChannelName, deviceToken, thirdPushMessage);
    }

    @Override
    public void push(String channelName, String deviceToken, ThirdPushMessage thirdPushMessage) {
        HuaweiChannel huaweiChannel = this.channelMap.get(channelName);
        if (huaweiChannel != null) {
            long currentTime = System.currentTimeMillis();
            String accessToken = huaweiChannel.getAccessToken();
            long tokenExpiredTime = huaweiChannel.getTokenExpiredTime();
            if (accessToken.isEmpty() || tokenExpiredTime < currentTime) {
                //如果accessToken失效,马上刷新
                this.updateAccessToken(channelName);
            }
            //
            if (thirdPushMessage.isValid()) {
                //body 消息
                String title = thirdPushMessage.getTitle();
                String content = thirdPushMessage.getContent();
                Map<String, Object> bodyMap = new HashMap(2, 1);
                bodyMap.put("title", title);
                bodyMap.put("content", content);
                //action param消息
                Map<String, Object> paramMap = new HashMap(2, 1);
                paramMap.put("appPkgName", huaweiChannel.getPackageName());
                //action消息
                Map<String, Object> actionMap = new HashMap(2, 1);
                long actionType = 3;
                actionMap.put("type", actionType);
                actionMap.put("param", paramMap);
                //消息体
                Map<String, Object> msgMap = new HashMap(4, 1);
                //系统通知消息,不透传
                long msgType = 3;
                msgMap.put("type", msgType);
                msgMap.put("body", bodyMap);
                msgMap.put("action", actionMap);
                //ext customize消息
                List<Map<String, Object>> customizeMapList = new ArrayList(1);
                Map<String, Object> customizeMap;
                Map<String, Object> extendMap = thirdPushMessage.getExtendMap();
                Set<Map.Entry<String, Object>> entrySet = extendMap.entrySet();
                String value;
                for (Map.Entry<String, Object> entry : entrySet) {
                    customizeMap = new HashMap(2, 1);
                    value = this.getStringValue(entry.getValue());
                    customizeMap.put(entry.getKey(), value);
                    customizeMapList.add(customizeMap);
                }
                //ext消息
                Map<String, Object> extMap = new HashMap(4, 1);
                extMap.put("biTag", "Trump");
                extMap.put("customize", customizeMapList);
                //hps消息体
                Map<String, Object> hpsMap = new HashMap(2, 1);
                hpsMap.put("msg", msgMap);
                hpsMap.put("ext", extMap);
                //payload消息体
                Map<String, Object> payloadMap = new HashMap(2, 1);
                payloadMap.put("hps", hpsMap);
                //推送
                String payload = "";
                ObjectMapper mapper = new ObjectMapper();
                try {
                    payload = mapper.writeValueAsString(payloadMap);
                    //构造http消息
                } catch (IOException ex) {
                }
                if (payload.isEmpty() == false) {
                    //开始构造http消息
                    Map<String, String> paramterMap = new HashMap(4, 1);
                    paramterMap.put("access_token", huaweiChannel.getAccessToken());
                    //
                    long nspTs = System.currentTimeMillis() / 1000l;
                    paramterMap.put("nsp_ts", Long.toString(nspTs));
                    //
                    paramterMap.put("nsp_svc", "openpush.message.api.send");
                    //
                    String deviceTokenList = "[\"" + deviceToken + "\"]";
                    paramterMap.put("device_token_list", deviceTokenList);
                    paramterMap.put("payload", payload);
                    //
                    Map<String, String> headerMap = new HashMap(2, 1);
                    headerMap.put("Content-Type", "application/x-www-form-urlencoded");
                    //
                    String nsp_ctx = "{\"ver\":\"1\", \"appId\":\"" + huaweiChannel.getAppId() + "\"}";
                    try {
                        nsp_ctx = URLEncoder.encode(nsp_ctx, "UTF-8");
                    } catch (UnsupportedEncodingException ex) {
                    }

                    String postUrl = this.apiUrl + "?nsp_ctx=" + nsp_ctx;
                    System.out.println(postUrl);
                    System.out.println(paramterMap);
                    System.out.println(headerMap);
                    String json = this.httpLocal.doPost(postUrl, paramterMap, headerMap);
                    System.out.println(json);
                    //处理返回结果
                    JsonNode rootNode = null;
                    if (json != null && json.isEmpty() == false) {
                        try {
                            rootNode = mapper.readValue(json, JsonNode.class);
                        } catch (IOException e) {
                            this.logger.error("huawei push response error:{}", json);
                        }
                    }
                    //
                    if (rootNode != null) {
                        JsonNode requestIdNode = rootNode.get("requestId");
                        if (requestIdNode == null) {
                            this.logger.error("huawei push accesstoken error:{}", json);
                        } else {
                            this.logger.debug("huawei push success:{}", json);
                        }
                    }
                }
            }
        } else {
            this.logger.error("huawei推送配置缺少appId,appSecret和packageName");
        }
    }
}
