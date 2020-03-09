package com.wolf.framework.worker.context;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.reponse.ResponseImpl;
import com.wolf.framework.reponse.WorkerResponse;
import com.wolf.framework.request.RequestImpl;
import com.wolf.framework.request.WorkerRequest;
import com.wolf.framework.utils.MapUtils;
import com.wolf.framework.utils.StringUtils;
import com.wolf.framework.worker.ServiceWorker;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.slf4j.Logger;

/**
 *
 * @author aladdin
 */
public abstract class AbstractWorkContext implements WorkerContext {

    //input
    private Map<String, Object> parameterMap = null;
    private final String route;
    private String callback = null;
    private String md5 = null;
    private boolean pretty = false;
    private final ServiceWorker serviceWorker;
    private final WorkerRequest request;
    private final WorkerResponse response;
    private final String ip;

    public AbstractWorkContext(String route, ServiceWorker serviceWorker, String ip) {
        this.route = route;
        this.serviceWorker = serviceWorker;
        this.request = new RequestImpl(this);
        this.response = new ResponseImpl(this);
        this.ip = ip;
    }

    public void initLocalParameter(Map<String, Object> parameterMap) {
        //
        Boolean prettyObj = MapUtils.getBooleanValue(parameterMap, "pretty");
        if (prettyObj != null) {
            this.pretty = prettyObj;
        }
        this.parameterMap = parameterMap;
    }

    private Object getValue(JsonNode jsonNode) {
        Object value;
        if (jsonNode.isObject()) {
            value = this.initObject(jsonNode);
        } else if (jsonNode.isArray()) {
            value = this.initArray((ArrayNode) jsonNode);
        } else if (jsonNode.isBoolean()) {
            value = jsonNode.asBoolean();
        } else if (jsonNode.isDouble()) {
            value = jsonNode.asDouble();
        } else if (jsonNode.isInt()) {
            long num = jsonNode.asInt();
            value = num;
        } else if (jsonNode.isLong()) {
            value = jsonNode.asLong();
        } else {
            value = StringUtils.trim(jsonNode.asText());
        }
        return value;
    }

    private Map<String, Object> initObject(JsonNode paramNode) {
        Map<String, Object> paramMap = new HashMap(8, 1);
        Iterator<Map.Entry<String, JsonNode>> iterator = paramNode.fields();
        Map.Entry<String, JsonNode> entry;
        String name;
        Object value;
        JsonNode jsonNode;
        while (iterator.hasNext()) {
            entry = iterator.next();
            name = entry.getKey();
            jsonNode = entry.getValue();
            if (jsonNode.isNull() == false) {
                value = this.getValue(jsonNode);
                paramMap.put(name, value);
            }
        }
        return paramMap;
    }

    private List<Object> initArray(ArrayNode paramNode) {
        List<Object> paramList = new ArrayList();
        Iterator<JsonNode> iterator = paramNode.elements();
        JsonNode jsonNode;
        Object value;
        while (iterator.hasNext()) {
            jsonNode = iterator.next();
            if (jsonNode.isNull() == false) {
                value = this.getValue(jsonNode);
                paramList.add(value);
            }
        }
        return paramList;
    }

    public void initWebsocketParameter(String json) {
        if (json.isEmpty() == false) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = null;
            try {
                rootNode = mapper.readValue(json, JsonNode.class);
            } catch (IOException e) {
                Logger logger = LogFactory.getLogger(FrameworkLogger.FRAMEWORK);
                logger.error("error json message:{}", json);
                logger.error("parse json error:", e);
            }
            if (rootNode != null) {
                //读取公共数据
                //callback
                JsonNode globalNode = rootNode.get("callback");
                if (globalNode != null) {
                    this.callback = globalNode.asText();
                }
                //md5
                globalNode = rootNode.get("md5");
                if (globalNode != null) {
                    this.md5 = globalNode.asText();
                }
                //
                globalNode = rootNode.get("pretty");
                if (globalNode != null) {
                    this.pretty = globalNode.asBoolean();
                }
                //读数据
                JsonNode paramNode = rootNode.get("param");
                if (paramNode == null || paramNode.isNull()) {
                    paramNode = rootNode;
                }
                this.parameterMap = new HashMap(8, 1);
                Map.Entry<String, JsonNode> entry;
                String name;
                Object value;
                JsonNode jsonNode;
                Iterator<Map.Entry<String, JsonNode>> iterator = paramNode.fields();
                while (iterator.hasNext()) {
                    entry = iterator.next();
                    name = entry.getKey();
                    jsonNode = entry.getValue();
                    if (jsonNode.isNull() == false) {
                        value = this.getValue(jsonNode);
                        this.parameterMap.put(name, value);
                    }
                }
            } else {
                this.parameterMap = Collections.emptyMap();
            }
        } else {
            this.parameterMap = Collections.emptyMap();
        }
    }

    public void initHttpParameter(Map<String, String> parameterMap, String json) {
        //读取公共数据
        //callback
        this.callback = parameterMap.get("callback");
        //
        this.md5 = parameterMap.get("md5");
        //
        String prettyStr = parameterMap.get("pretty");
        if (prettyStr != null && prettyStr.toLowerCase().equals("true")) {
            this.pretty = true;
        }
        if (json != null && json.isEmpty() == false) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = null;
            try {
                rootNode = mapper.readValue(json, JsonNode.class);
            } catch (IOException e) {
                Logger logger = LogFactory.getLogger(FrameworkLogger.FRAMEWORK);
                logger.error("error json message:{}", json);
                logger.error("parse json error:", e);
            }
            if (rootNode != null) {
                //读数据
                this.parameterMap = new HashMap(8, 1);
                Map.Entry<String, JsonNode> entry;
                String name;
                Object value;
                JsonNode jsonNode;
                Iterator<Map.Entry<String, JsonNode>> iterator = rootNode.fields();
                while (iterator.hasNext()) {
                    entry = iterator.next();
                    name = entry.getKey();
                    jsonNode = entry.getValue();
                    if (jsonNode.isNull() == false) {
                        value = this.getValue(jsonNode);
                        this.parameterMap.put(name, value);
                    }
                }
            }
        }
        if (this.parameterMap == null) {
            this.parameterMap = new HashMap(parameterMap.size(), 1);
        }
        //两类参数合并
        Set<Entry<String, String>> entrySet = parameterMap.entrySet();
        for (Entry<String, String> entry : entrySet) {
            if (this.parameterMap.containsKey(entry.getKey()) == false) {
                this.parameterMap.put(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public ServiceWorker getServiceWorker() {
        return this.serviceWorker;
    }

    @Override
    public final String getRoute() {
        return route;
    }

    @Override
    public final Map<String, Object> getParameterMap() {
        return Collections.unmodifiableMap(this.parameterMap);
    }

    @Override
    public final Object getParameter(String name) {
        return this.parameterMap.get(name);
    }

    @Override
    public final ApplicationContext getApplicationContext() {
        return ApplicationContext.CONTEXT;
    }

    @Override
    public WorkerRequest getWorkerRequest() {
        return this.request;
    }

    @Override
    public WorkerResponse getWorkerResponse() {
        return this.response;
    }

    @Override
    public String getCallback() {
        return callback;
    }

    @Override
    public String getMd5() {
        return md5;
    }

    @Override
    public boolean isPretty() {
        return pretty;
    }

    @Override
    public String getIp() {
        return this.ip;
    }

}
