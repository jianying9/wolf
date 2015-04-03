package com.wolf.framework.worker.context;

import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.utils.StringUtils;
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

    public AbstractWorkContext(String route, String json) {
        this.route = route;
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
    }

    public AbstractWorkContext(String act, Map<String, String> parameterMap) {
        this.route = act;
        if (parameterMap != null) {
            this.parameterMap = parameterMap;
        } else {
            this.parameterMap = Collections.emptyMap();
        }
    }

    @Override
    public final String getRoute() {
        return route;
    }

    public final String getParameter(String name) {
        return this.parameterMap.get(name);
    }

    @Override
    public final Map<String, String> getParameterMap() {
        return this.parameterMap;
    }

    public final ApplicationContext getApplicationContext() {
        return ApplicationContext.CONTEXT;
    }
}
