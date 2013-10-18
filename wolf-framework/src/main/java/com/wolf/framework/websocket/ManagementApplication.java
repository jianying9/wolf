package com.wolf.framework.websocket;

import com.sun.grizzly.tcp.Request;
import com.sun.grizzly.websockets.DataFrame;
import com.sun.grizzly.websockets.WebSocket;
import com.sun.grizzly.websockets.WebSocketApplication;
import com.wolf.framework.config.FrameworkLoggerEnum;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.utils.StringUtils;
import com.wolf.framework.worker.ServiceWorker;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;

/**
 *
 * @author aladdin
 */
public final class ManagementApplication extends WebSocketApplication {

    private final Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.FRAMEWORK);

    @Override
    public boolean isApplicationRequest(Request request) {
        final String uri = request.requestURI().toString();
        return uri.endsWith("/management.io");
    }

    @Override
    public void onConnect(WebSocket socket) {
    }

    @Override
    public void onClose(WebSocket socket, DataFrame frame) {
        socket.close();
    }

    @Override
    public void onMessage(WebSocket socket, String text) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readValue(text, JsonNode.class);
        } catch (IOException e) {
            logger.error("error json message:{}", text);
            logger.error("parse json error:", e);
        }
        if (rootNode != null) {
            String result = "{\"flag\":\"EXCEPTION\",\"error\":\"error api\"}";
            //读数据
            Map<String, String> parameterMap = new HashMap<String, String>(4, 1);
            Entry<String, JsonNode> entryNode;
            String name;
            String value;
            Iterator<Map.Entry<String, JsonNode>> iterator = rootNode.getFields();
            while (iterator.hasNext()) {
                entryNode = iterator.next();
                name = entryNode.getKey();
                value = entryNode.getValue().getTextValue();
                value = StringUtils.trim(value);
                parameterMap.put(name, value);
            }
            //返回所有的接口分组信息:act=GROUPS
            //返回所有接口信息：act=SERVICES&group=xxx
            //返回某个接口信息：act=INFO&actinoName=xxx
            String act = parameterMap.get("act");
            if (act != null) {
                if (act.equals("GROUPS")) {
                    result = this.getGroups(parameterMap);
                } else if (act.equals("SERVICES")) {
                    //返回所有接口信息
                    result = this.getServices(parameterMap);
                } else if (act.equals("INFO")) {
                    //返回接口信息
                    result = this.getInfo(parameterMap);
                }
            }
            socket.send(result);
        }
        socket.close();
    }

    private String getGroups(Map<String, String> parameterMap) {
        Map<String, ServiceWorker> serviceWorkerMap = ApplicationContext.CONTEXT.getServiceWorkerMap();
        Set<Entry<String, ServiceWorker>> entrySet = serviceWorkerMap.entrySet();
        StringBuilder resultBuilder = new StringBuilder(entrySet.size() * 32);
        Set<String> groupSet = new HashSet<String>(16, 1);
        ServiceWorker serviceWorker;
        for (Entry<String, ServiceWorker> entryService : entrySet) {
            serviceWorker = entryService.getValue();
            groupSet.add(serviceWorker.getGroup());
        }
        resultBuilder.append("{\"act\":\"GROUPS\",\"data\":[");
        if (groupSet.isEmpty() == false) {
            for (String group : groupSet) {
                resultBuilder.append("{\"groupName\":\"").append(group)
                        .append("\"}").append(',');
            }
            resultBuilder.setLength(resultBuilder.length() - 1);
        }
        resultBuilder.append("]}");
        String result = resultBuilder.toString();
        return result;
    }

    private String getServices(Map<String, String> parameterMap) {
        String result;
        String group = parameterMap.get("group");
        if (group == null) {
            result = "{\"flag\":\"INVALID\",\"act\":\"SERVICES\",\"error\":\"group is null\"}";
        } else {
            Map<String, ServiceWorker> serviceWorkerMap = ApplicationContext.CONTEXT.getServiceWorkerMap();
            Set<Entry<String, ServiceWorker>> entrySet = serviceWorkerMap.entrySet();
            StringBuilder resultBuilder = new StringBuilder(entrySet.size() * 32);
            String actionName;
            ServiceWorker serviceWorker;
            resultBuilder.append("{\"act\":\"SERVICES\",\"group\":\"").append(group)
                    .append("\",\"data\":[");
            boolean hasResult = false;
            if (entrySet.isEmpty() == false) {
                for (Entry<String, ServiceWorker> entryService : entrySet) {
                    actionName = entryService.getKey();
                    serviceWorker = entryService.getValue();
                    if (serviceWorker.getGroup().equals(group)) {
                        hasResult = true;
                        resultBuilder.append("{\"actionName\":\"").append(actionName)
                                .append("\",\"description\":\"").append(serviceWorker.getDescription())
                                .append("\"}").append(',');
                    }
                }
                if (hasResult) {
                    resultBuilder.setLength(resultBuilder.length() - 1);
                }
            }
            resultBuilder.append("]}");
            result = resultBuilder.toString();
        }
        return result;
    }

    private String getInfo(Map<String, String> parameterMap) {
        String result;
        String actionName = parameterMap.get("actionName");
        if (actionName == null) {
            result = "{\"flag\":\"INVALID\",\"act\":\"INFO\",\"error\":\"actionName is null\"}";
        } else {
            ServiceWorker serviceWorker = ApplicationContext.CONTEXT.getServiceWorker(actionName);
            if (serviceWorker != null) {
                String info = serviceWorker.getInfo();
                StringBuilder resultBuilder = new StringBuilder(info.length() + 20);
                resultBuilder.append("{\"act\":\"INFO\",\"actionName\":\"")
                        .append(actionName).append("\",\"data\":")
                        .append(info).append("}");
                result = resultBuilder.toString();
            } else {
                result = "{\"flag\":\"INVALID\",\"act\":\"INFO\",\"error\":\"actionName not exist\"}";
            }
        }
        return result;
    }
}
