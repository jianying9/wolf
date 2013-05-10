package com.wolf.framework.worker.context;

import com.wolf.framework.config.DefaultResponseFlagEnum;
import com.wolf.framework.config.FrameworkLoggerEnum;
import com.wolf.framework.config.ResponseFlagType;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.service.parameter.ParameterHandler;
import com.wolf.framework.session.Session;
import com.wolf.framework.utils.JsonUtils;
import com.wolf.framework.utils.StringUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;

/**
 *
 * @author aladdin
 */
public abstract class AbstractMessageContext {

    //page
    private int pageIndex = 1;
    private int pageSize = 15;
    private int pageTotal = 0;
    private int pageNum = 0;
    //input
    private final Map<String, String> parameterMap = new HashMap<String, String>(8, 1);
    private final String act;
    //message
    private String error = "";
    protected String responseMessage = "";
    private ResponseFlagType flag = DefaultResponseFlagEnum.FAILURE;
    private Map<String, String> mapData;
    private List<Map<String, String>> mapListData;
    //broadcast
    protected List<String> broadcastUserIdList;
    //session
    protected Session newSession;

    public AbstractMessageContext(String act, String json) {
        this.act = act;
        if (json.isEmpty() == false) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = null;
            try {
                rootNode = mapper.readValue(json, JsonNode.class);
            } catch (IOException e) {
                Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.FRAMEWORK);
                logger.error("error json message:{}", json);
                logger.error("parse json error:", e);
            }
            if (rootNode != null) {
                //读数据
                Map.Entry<String, JsonNode> entry;
                String name;
                String value;
                Iterator<Map.Entry<String, JsonNode>> iterator = rootNode.getFields();
                while (iterator.hasNext()) {
                    entry = iterator.next();
                    name = entry.getKey();
                    value = entry.getValue().getTextValue();
                    value = StringUtils.trim(value);
                    this.parameterMap.put(name, value);
                }
            }
        }
    }

    public AbstractMessageContext(String act, Map<String, String> parameterMap) {
        this.act = act;
        if (parameterMap != null) {
            this.parameterMap.putAll(parameterMap);
        }
    }

    public final int getPageIndex() {
        return pageIndex;
    }

    public final void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex > 0 ? pageIndex : 1;
    }

    public final int getPageSize() {
        return pageSize;
    }

    public final void setPageSize(int pageSize) {
        this.pageSize = pageSize > 0 ? pageSize : 15;
    }

    public final int getPageTotal() {
        return pageTotal;
    }

    public final void setPageTotal(int pageTotal) {
        this.pageTotal = pageTotal;
    }

    public final int getPageNum() {
        return pageNum;
    }

    public final void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public final String getAct() {
        return act;
    }

    public final String getParameter(String name) {
        return this.parameterMap.get(name);
    }

    public final void putParameter(String name, String value) {
        this.parameterMap.put(name, value);
    }

    public final void removeParameter(String name) {
        this.parameterMap.remove(name);
    }

    public final Map<String, String> getParameterMap() {
        return this.parameterMap;
    }

    public final void invalid() {
        this.flag = DefaultResponseFlagEnum.INVALID;
    }

    public final void unlogin() {
        this.flag = DefaultResponseFlagEnum.UNLOGIN;
    }

    public final void success() {
        this.flag = DefaultResponseFlagEnum.SUCCESS;
    }

    public final void setFlag(ResponseFlagType responseFlagType) {
        this.flag = responseFlagType;
    }

    public final void setError(String error) {
        this.error = error;
    }

    public final void setMapData(Map<String, String> parameterMap) {
        this.mapData = parameterMap;
        this.mapListData = null;
    }

    public final void setMapListData(List<Map<String, String>> parameterMapList) {
        this.mapData = null;
        this.mapListData = parameterMapList;
    }

    public final <T extends Entity> void setEntityData(T t) {
        Map<String, String> entityMap = t.toMap();
        this.setMapData(entityMap);
    }

    public final <T extends Entity> void setEntityListData(List<T> tList) {
        List<Map<String, String>> entityMapList = new ArrayList<Map<String, String>>(tList.size());
        for (T t : tList) {
            entityMapList.add(t.toMap());
        }
        this.setMapListData(entityMapList);
    }

    public final void addBroadcastUserId(String broadcastUserId) {
        if (this.broadcastUserIdList == null) {
            broadcastUserIdList = new ArrayList<String>(10);
        }
        this.broadcastUserIdList.add(broadcastUserId);
    }

    public final void addBroadcastUserIdList(List<String> broadcastUserIdList) {
        if (this.broadcastUserIdList == null) {
            this.broadcastUserIdList = broadcastUserIdList;
        } else {
            this.broadcastUserIdList.addAll(broadcastUserIdList);
        }
    }

    public final void setNewSession(Session session) {
        this.newSession = session;
    }

    public final ApplicationContext getApplicationContext() {
        return ApplicationContext.CONTEXT;
    }

    public final void createErrorMessage() {
        StringBuilder jsonBuilder = new StringBuilder(128);
        jsonBuilder.append("{\"flag\":\"").append(this.flag.getFlagName());
        jsonBuilder.append("\",\"act\":\"").append(this.act);
        jsonBuilder.append("\",\"error\":\"").append(this.error).append("\"}");
        this.responseMessage = jsonBuilder.toString();
    }

    public final void createMessage(String[] parameterNames, Map<String, ParameterHandler> parameterHandlerMap) {
        StringBuilder jsonBuilder = new StringBuilder(64);
        jsonBuilder.append("{\"flag\":\"").append(this.flag.getFlagName());
        jsonBuilder.append("\",\"act\":\"").append(this.act);
        String data = "{}";
        if (parameterNames.length > 0) {
            if (this.mapData != null) {
                data = JsonUtils.mapToJSON(this.mapData, parameterNames, parameterHandlerMap);
            } else if (this.mapListData != null) {
                data = JsonUtils.mapListToJSON(this.mapListData, parameterNames, parameterHandlerMap);
            }
        }
        jsonBuilder.append("\",\"data\":").append(data).append('}');
        this.responseMessage = jsonBuilder.toString();
    }

    public final void createPageMessage(String[] parameterNames, Map<String, ParameterHandler> parameterHandlerMap) {
        StringBuilder jsonBuilder = new StringBuilder(128);
        jsonBuilder.append("{\"flag\":\"").append(this.flag.getFlagName());
        jsonBuilder.append("\",\"act\":\"").append(this.act);
        jsonBuilder.append("\",\"pageTotal\":").append(this.pageTotal);
        jsonBuilder.append(",\"pageIndex\":").append(this.pageIndex);
        jsonBuilder.append(",\"pageSize\":").append(this.pageSize);
        jsonBuilder.append(",\"pageNum\":").append(this.pageNum);
        String data = "";
        if (parameterNames.length > 0) {
            if (this.mapData != null) {
                data = JsonUtils.mapToJSON(this.mapData, parameterNames, parameterHandlerMap);
            } else if (this.mapListData != null) {
                data = JsonUtils.mapListToJSON(this.mapListData, parameterNames, parameterHandlerMap);
            }
        }
        jsonBuilder.append(",\"data\":[").append(data).append("]}");
        this.responseMessage = jsonBuilder.toString();
    }
}
