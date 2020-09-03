package com.wolf.framework.dao.elasticsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.dao.ColumnHandler;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import com.wolf.elasticsearch.index.query.QueryBuilder;
import com.wolf.elasticsearch.search.sort.SortBuilder;

/**
 *
 * @author jianying9
 * @param <T>
 */
public abstract class AbstractEsEntityDao<T extends Entity> implements EsEntityDao<T>
{

    protected final EsColumnHandler keyHandler;
    protected final List<EsColumnHandler> columnHandlerList;
    protected final Set<String> allColumnNameSet;
    protected final Class<T> clazz;
    protected final Logger logger = LogFactory.getLogger(FrameworkLogger.DAO);
    protected final String index;
    protected final String type;
    protected final RestClient restClient;

    public AbstractEsEntityDao(
            RestClient restClient,
            String index,
            String type,
            EsColumnHandler keyHandler,
            List<EsColumnHandler> columnHandlerList,
            Class<T> clazz)
    {
        this.columnHandlerList = columnHandlerList;
        this.keyHandler = keyHandler;
        this.index = index;
        this.type = type;
        this.restClient = restClient;
        this.clazz = clazz;
        this.allColumnNameSet = new HashSet();
        this.allColumnNameSet.add(this.keyHandler.getColumnName());
        this.columnHandlerList.forEach((esColumnHandler) -> {
            this.allColumnNameSet.add(esColumnHandler.getColumnName());
        });
    }

    protected final String getKeyValue(Object value)
    {
        String v = "";
        if (String.class.isInstance(value)) {
            v = (String) value;
        } else if (Integer.class.isInstance(value)) {
            Integer i = (Integer) value;
            v = i.toString();
        } else if (Long.class.isInstance(value)) {
            Long l = (Long) value;
            v = l.toString();
        } else if (Boolean.class.isInstance(value)) {
            Boolean b = (Boolean) value;
            v = b.toString();
        } else if (Double.class.isInstance(value)) {
            Double d = (Double) value;
            v = d.toString();
        }
        return v;
    }

    @Override
    public String getIndex()
    {
        return index;
    }

    @Override
    public String getType()
    {
        return type;
    }

    @Override
    public EsColumnHandler getKeyHandler()
    {
        return keyHandler;
    }

    @Override
    public List<EsColumnHandler> getColumnHandlerList()
    {
        return columnHandlerList;
    }

    @Override
    public final T insertAndInquire(Map<String, Object> entityMap)
    {
        String id = this.insert(entityMap);
        return this.inquireByKey(id);
    }

    @Override
    public final T updateAndInquire(Map<String, Object> entityMap)
    {
        String id = this.update(entityMap);
        return this.inquireByKey(id);
    }

    @Override
    public final void delete(Object keyValue)
    {
        String id = this.getKeyValue(keyValue);
        String path = "/" + index + "/" + type + "/" + id + "?refresh=true";
        try {
            Request request = new Request("DELETE", path);
            restClient.performRequest(request);
        } catch (IOException ex) {
        }
    }

    @Override
    public final boolean exist(Object keyValue)
    {
        boolean exist = false;
        String id = this.getKeyValue(keyValue);
        String path = "/" + index + "/" + type + "/" + id;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Request request = new Request("GET", path);
            Response response = restClient.performRequest(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
            exist = responseMap.containsKey("_source");
        } catch (IOException ex) {
        }
        return exist;
    }

    protected final T parseMap(Map<String, Object> entityMap)
    {
        T t = null;
        if (entityMap != null) {
            Object value;
            try {
                t = this.clazz.newInstance();
                value = entityMap.get(this.keyHandler.getColumnName());
                keyHandler.setFieldValue(t, value);
                for (ColumnHandler column : this.columnHandlerList) {
                    value = entityMap.get(column.getColumnName());
                    if (value == null) {
                        value = column.getDefaultValue();
                    }
                    column.setFieldValue(t, value);
                }
            } catch (InstantiationException | IllegalAccessException ex) {
            }
        }
        return t;
    }

    /**
     *
     * @param queryBuilder
     * @param from
     * @param size
     * @return
     */
    @Override
    public final List<T> search(QueryBuilder queryBuilder, int from, int size)
    {
        SortBuilder sort = null;
        return this.search(queryBuilder, sort, from, size);
    }

    @Override
    public final List<T> search(SortBuilder sort, int from, int size)
    {
        return this.search(null, sort, from, size);
    }

    @Override
    public final List<T> search(int from, int size)
    {
        SortBuilder sort = null;
        return this.search(null, sort, from, size);
    }

    /**
     *
     * @param queryBuilder
     * @return
     */
    @Override
    public final List<T> search(QueryBuilder queryBuilder)
    {
        int size = 100;
        int from = 0;
        SortBuilder sort = null;
        return this.search(queryBuilder, sort, from, size);
    }

    @Override
    public final Map<String, Object> getFieldMap(EsColumnHandler esColumnHandler)
    {
        Map<String, Object> filedMap = new HashMap(2, 1);
        EsColumnDataType esColumnDataType = esColumnHandler.getEsColumnDataType();
        String fieldType = esColumnDataType.name().toLowerCase();
        filedMap.put("type", fieldType);
        if (esColumnDataType.equals(EsColumnDataType.TEXT)) {
            if (esColumnHandler.isAnalyzer()) {
                filedMap.put("analyzer", "ik_max_word");
            }
        }
        return filedMap;
    }

}
