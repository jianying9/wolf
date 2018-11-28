package com.wolf.thirdparty.push;

import com.wolf.framework.config.FrameworkConfig;
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
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;

/**
 *
 * @author jianying9
 */
@LocalServiceConfig
public class HuaweiLocalImpl implements HuaweiLocal, Resource {

    private String appId = "";
    private String appSecret = "";
    private String packageName = "";

    private final Logger logger = LogFactory.getLogger(FrameworkLogger.XIAOMI);

    private final String tokenUrl = "https://login.cloud.huawei.com/oauth2/v2/token"; //获取认证Token的URL

    private final String apiUrl = "https://api.push.hicloud.com/pushsend.do"; //应用级消息下发API 

    //
    private String accessToken = null;
    private long tokenExpiredTime = 0;

    @InjectLocalService
    private HttpLocal httpLocal;

    @Override
    public void init() {
        String value = ApplicationContext.CONTEXT.getParameter(ThirdPushConfig.HUAWEI_APP_ID);
        if (value != null) {
            this.appId = value;
        }
        //
        value = ApplicationContext.CONTEXT.getParameter(ThirdPushConfig.HUAWEI_APP_SECRET);
        if (value != null) {
            this.appSecret = value;
        }
        //
        value = ApplicationContext.CONTEXT.getParameter(ThirdPushConfig.HUAWEI_PACKAGE_NAME);
        if (value != null) {
            this.packageName = value;
        }
        String compileModel = ApplicationContext.CONTEXT.getParameter(FrameworkConfig.COMPILE_MODEL);
        if (compileModel.equals(FrameworkConfig.UNIT_TEST) == false) {
            //启动更新一次token
            this.updateAccessToken();
        } else {
            System.out.println("unit模式不初始化huawei push");
        }
    }

    @Override
    public void destory() {
    }

    @Override
    public void updateAccessToken() {
        if (this.appId.isEmpty() == false && this.appSecret.isEmpty() == false && this.packageName.isEmpty() == false) {
            //获取token
            Map<String, String> paramterMap = new HashMap(4, 1);
            paramterMap.put("grant_type", "client_credentials");
            paramterMap.put("client_secret", this.appSecret);
            paramterMap.put("client_id", this.appId);
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
                    this.accessToken = accessTokenNode.getTextValue();
                    //
                    JsonNode expiresInNode = rootNode.get("expires_in");
                    long expiresIn = expiresInNode.getLongValue();
                    this.tokenExpiredTime = System.currentTimeMillis() + expiresIn * 900l;
                    this.logger.info("huawei update accesstoken success:{},{}", this.accessToken, Long.toString(this.tokenExpiredTime));
                } else {
                    this.logger.error("huawei update accesstoken error:{}", json);
                }
            }
        } else {
            this.logger.error("huawei推送配置缺少appId,appSecret和packageName");
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
    public void push(String deviceToken, ThirdPushMessage thirdPushMessage) {
        if (this.appId.isEmpty() == false && this.appSecret.isEmpty() == false && this.packageName.isEmpty() == false) {
            long currentTime = System.currentTimeMillis();
            if (this.accessToken.isEmpty() || this.tokenExpiredTime < currentTime) {
                //如果accessToken失效,马上刷新
                this.updateAccessToken();
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
                paramMap.put("appPkgName", this.packageName);
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
                    paramterMap.put("access_token", this.accessToken);
                    //
                    long nspTs = System.currentTimeMillis() / 1000l;
                    paramterMap.put("nsp_ts", Long.toString(nspTs));
                    //
                    paramterMap.put("nsp_svc", "openpush.message.api.send");
                    //
                    String deviceTokenList = "[\"" + deviceToken + "\"]";
                    paramterMap.put("device_token_list", deviceTokenList);
                    //
                    paramterMap.put("payload", payload);
                    //
                    Map<String, String> headerMap = new HashMap(2, 1);
                    headerMap.put("Content-Type", "application/x-www-form-urlencoded");
                    //
                    String nsp_ctx = "{\"ver\":\"1\", \"appId\":\"" + this.appId + "\"}";
                    try {
                        nsp_ctx = URLEncoder.encode(nsp_ctx, "utf-8");
                    } catch (UnsupportedEncodingException ex) {
                    }

                    String postUrl = this.apiUrl + "?nsp_ctx=" + nsp_ctx;
                    String json = this.httpLocal.doPost(postUrl, paramterMap, headerMap);
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
