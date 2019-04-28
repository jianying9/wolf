package com.wolf.framework.dao.elasticsearch;

import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.dao.ColumnHandler;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;

/**
 *
 * @author jianying9
 * @param <T>
 */
public abstract class AbstractEsEntityDao<T extends Entity> implements EsEntityDao<T> {

    protected final EsColumnHandler keyHandler;
    protected final List<EsColumnHandler> columnHandlerList;
    protected final Class<T> clazz;
    protected final Logger logger = LogFactory.getLogger(FrameworkLogger.DAO);
    protected final String index;
    protected final String type;
    protected final TransportClient transportClient;

    public AbstractEsEntityDao(
            TransportClient transportClient,
            String index,
            String type,
            EsColumnHandler keyHandler,
            List<EsColumnHandler> columnHandlerList,
            Class<T> clazz) {
        this.columnHandlerList = columnHandlerList;
        this.keyHandler = keyHandler;
        this.index = index;
        this.type = type;
        this.transportClient = transportClient;
        this.clazz = clazz;
    }

    protected final String getKeyValue(Object value) {
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

    protected final void refresh() {
        this.transportClient.admin().indices().prepareRefresh(index).get();
    }

    @Override
    public String getIndex() {
        return index;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public final T insertAndInquire(Map<String, Object> entityMap) {
        String id = this.insert(entityMap);
        return this.inquireByKey(id);
    }

    @Override
    public final T updateAndInquire(Map<String, Object> entityMap) {
        String id = this.update(entityMap);
        return this.inquireByKey(id);
    }

    @Override
    public final void delete(Object keyValue) {
        String id = this.getKeyValue(keyValue);
        this.transportClient.prepareDelete(index, type, id).get();
    }

    @Override
    public final void batchDelete(List<Object> keyValues) {
        BulkRequestBuilder bulkRequestBuilder = this.transportClient.prepareBulk();
        String id;
        DeleteRequest deleteRequest;
        for (Object keyValue : keyValues) {
            id = this.getKeyValue(keyValue);
            deleteRequest = this.transportClient.prepareDelete(index, type, id).request();
            bulkRequestBuilder.add(deleteRequest);
        }
        bulkRequestBuilder.get();
        //
        this.refresh();
    }

    @Override
    public final boolean exist(Object keyValue) {
        boolean exist = false;
        String id = this.getKeyValue(keyValue);
        GetResponse getResponse = this.transportClient.prepareGet(index, type, id).get();
        if (getResponse != null && getResponse.isExists()) {
            exist = true;
        }
        return exist;
    }

    protected final T parseMap(Map<String, Object> entityMap) {
        T t = null;
        if (entityMap != null) {
            Object value;
            try {
                t = this.clazz.newInstance();
                value = entityMap.get(this.keyHandler.getColumnName());
                keyHandler.setFieldValue(t, value);
                for (ColumnHandler column : this.columnHandlerList) {
                    value = entityMap.get(column.getColumnName());
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
    public final List<T> search(QueryBuilder queryBuilder, int from, int size) {
        return this.search(queryBuilder, null, from, size);
    }

    @Override
    public final List<T> search(SortBuilder sort, int from, int size) {
        return this.search(null, sort, from, size);
    }

    @Override
    public final List<T> search(int from, int size) {
        return this.search(null, null, from, size);
    }

    /**
     *
     * @param queryBuilder
     * @return
     */
    @Override
    public final List<T> search(QueryBuilder queryBuilder) {
        int size = 100;
        int from = 0;
        return this.search(queryBuilder, null, from, size);
    }

    protected final Map<String, Object> getFieldMap(EsColumnHandler esColumnHandler) {
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

    @Override
    public final String check() {
        String result = "";
        //创建index
        IndicesExistsResponse indicesExistsResponse = this.transportClient.admin().indices().prepareExists(this.index).get();
        if (indicesExistsResponse.isExists() == false) {
            System.out.println("创建index:" + this.index);
            transportClient.admin().indices().prepareCreate(this.index).get();
        }
        //
        Map<String, Object> typeMap = new HashMap();
        //构造属性
        Map<String, Object> propertyMap = new HashMap();
        Map<String, Object> keyMap = this.getFieldMap(keyHandler);
        propertyMap.put(keyHandler.getColumnName(), keyMap);
        //
        Map<String, Object> fieldMap;
        for (EsColumnHandler esColumnHandler : columnHandlerList) {
            fieldMap = this.getFieldMap(esColumnHandler);
            propertyMap.put(esColumnHandler.getColumnName(), fieldMap);
        }
        typeMap.put("properties", propertyMap);
        //关闭_all
        Map<String, Object> allMap = new HashMap(2, 1);
        allMap.put("enabled", false);
        typeMap.put("_all", allMap);
        //
        Map<String, Object> indexMap = new HashMap();
        indexMap.put(type, typeMap);
        //
        ObjectMapper mapper = new ObjectMapper();
        String json = "{}";
        try {
            json = mapper.writeValueAsString(indexMap);
        } catch (IOException ex) {
        }
        System.out.println("更新type:" + this.type);
        System.out.println(json);
        PutMappingResponse response = this.transportClient.admin().indices().preparePutMapping(index)
                .setType(this.type)
                .setSource(json, XContentType.JSON)
                .get();
        return result;
    }

}
