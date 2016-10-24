package com.wolf.framework.worker.context;

import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.reponse.ResponseImpl;
import com.wolf.framework.reponse.WorkerResponse;
import com.wolf.framework.request.RequestImpl;
import com.wolf.framework.request.WorkerRequest;
import com.wolf.framework.utils.StringUtils;
import com.wolf.framework.worker.ServiceWorker;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;

/**
 *
 * @author aladdin
 */
public abstract class AbstractWorkContext implements WorkerContext {

    //input
    private final Map<String, String> parameterMap;
    private final String route;
    private final ServiceWorker serviceWorker;
    private final WorkerRequest request;
    private final WorkerResponse response;

    public AbstractWorkContext(String route, String json, ServiceWorker serviceWorker) {
        this.route = route;
        this.serviceWorker = serviceWorker;
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
                //读数据
                this.parameterMap = new HashMap<String, String>(8, 1);
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
            } else {
                this.parameterMap = Collections.emptyMap();
            }
        } else {
            this.parameterMap = Collections.emptyMap();
        }
        this.request = new RequestImpl(this);
        this.response = new ResponseImpl(this);
    }

    public AbstractWorkContext(String route, Map<String, String> parameterMap, ServiceWorker serviceWorker) {
        this.route = route;
        this.serviceWorker = serviceWorker;
        if (parameterMap != null) {
            this.parameterMap = parameterMap;
        } else {
            this.parameterMap = Collections.emptyMap();
        }
        this.request = new RequestImpl(this);
        this.response = new ResponseImpl(this);
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
    public final Map<String, String> getParameterMap() {
        return this.parameterMap;
    }
    
    @Override
    public final String getParameter(String name) {
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
}
