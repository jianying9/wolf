package com.wolf.framework.dao.elasticsearch;

import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.dao.ColumnHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;

/**
 *
 * @author jianying9
 * @param <T>
 */
public class EsEntityDaoImpl<T extends Entity> implements EsEntityDao<T> {

    protected final EsColumnHandler keyHandler;
    protected final List<EsColumnHandler> columnHandlerList;
    protected final Class<T> clazz;
    protected final Logger logger = LogFactory.getLogger(FrameworkLogger.DAO);
    protected final String index;
    protected final String type;
    private final TransportClient transportClient;

    public EsEntityDaoImpl(
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

    private String getKeyValue(Object value) {
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

    private void refresh() {
        this.transportClient.admin().indices().prepareRefresh(index).get();
    }

    @Override
    public String insert(Map<String, Object> entityMap) {
        Object keyValue = entityMap.get(this.keyHandler.getColumnName());
        if (keyValue == null) {
            throw new RuntimeException("Can not find keyValue when insert:" + entityMap.toString());
        }
        String id = this.getKeyValue(keyValue);
        this.transportClient.prepareIndex(index, type, id).setSource(entityMap).get();
        //
        this.refresh();
        return id;
    }

    @Override
    public T insertAndInquire(Map<String, Object> entityMap) {
        String id = this.insert(entityMap);
        return this.inquireByKey(id);
    }

    @Override
    public void batchInsert(List<Map<String, Object>> entityMapList) {
        BulkRequestBuilder bulkRequestBuilder = this.transportClient.prepareBulk();
        String id;
        IndexRequest indexRequest;
        Object keyValue;
        for (Map<String, Object> entityMap : entityMapList) {
            keyValue = entityMap.get(this.keyHandler.getColumnName());
            if (keyValue == null) {
                throw new RuntimeException("Can not find keyValue when insert:" + entityMap.toString());
            }
            id = this.getKeyValue(keyValue);
            indexRequest = this.transportClient.prepareIndex(index, type, id).setSource(entityMap).request();
            bulkRequestBuilder.add(indexRequest);
        }
        bulkRequestBuilder.get();
        //
        this.refresh();
    }

    @Override
    public String update(Map<String, Object> entityMap) {
        Object keyValue = entityMap.get(this.keyHandler.getColumnName());
        if (keyValue == null) {
            throw new RuntimeException("Can not find keyValue when update:" + entityMap.toString());
        }
        String id = this.getKeyValue(keyValue);
        this.transportClient.prepareUpdate(index, type, id).setDoc(entityMap).get();
        //
        this.refresh();
        return id;
    }

    @Override
    public void batchUpdate(List<Map<String, Object>> entityMapList) {
        BulkRequestBuilder bulkRequestBuilder = this.transportClient.prepareBulk();
        String id;
        UpdateRequest updateRequest;
        Object keyValue;
        for (Map<String, Object> entityMap : entityMapList) {
            keyValue = entityMap.get(this.keyHandler.getColumnName());
            if (keyValue == null) {
                throw new RuntimeException("Can not find keyValue when update:" + entityMap.toString());
            }
            id = this.getKeyValue(keyValue);
            updateRequest = this.transportClient.prepareUpdate(index, type, id).setDoc(entityMap).request();
            bulkRequestBuilder.add(updateRequest);
        }
        bulkRequestBuilder.get();
        //
        this.refresh();
    }

    @Override
    public T updateAndInquire(Map<String, Object> entityMap) {
        String id = this.update(entityMap);
        return this.inquireByKey(id);
    }

    @Override
    public void batchDelete(List<Object> keyValues) {
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
    public boolean exist(Object keyValue) {
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

    @Override
    public T inquireByKey(Object keyValue) {
        T t = null;
        String id = this.getKeyValue(keyValue);
        GetResponse getResponse = this.transportClient.prepareGet(index, type, id).get();
        if (getResponse != null && getResponse.isExists()) {
            Map<String, Object> entityMap = getResponse.getSourceAsMap();
            t = this.parseMap(entityMap);
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
    public List<T> search(QueryBuilder queryBuilder, int from, int size) {
        return this.search(queryBuilder, null, from, size);
    }

    /**
     *
     * @param queryBuilder
     * @param sort
     * @param from
     * @param size
     * @return
     */
    @Override
    public List<T> search(QueryBuilder queryBuilder, SortBuilder sort, int from, int size) {
        if (size >= 100) {
            size = 100;
        }
        SearchRequestBuilder searchRequestBuilder = this.transportClient.prepareSearch(index)
                .setTypes(type)
                .setFrom(from)
                .setSize(size)
                .setQuery(queryBuilder);
        if (sort != null) {
            searchRequestBuilder.addSort(sort);
        }
        SearchResponse response = searchRequestBuilder.get();
        SearchHits searchHits = response.getHits();
        SearchHit[] searchHitArray = searchHits.getHits();
        List<T> tList = new ArrayList(searchHitArray.length);
        Map<String, Object> entityMap;
        T t;
        for (SearchHit searchHit : searchHitArray) {
            entityMap = searchHit.getSourceAsMap();
            t = this.parseMap(entityMap);
            tList.add(t);
        }
        return tList;
    }

    /**
     *
     * @param queryBuilder
     * @return
     */
    @Override
    public List<T> search(QueryBuilder queryBuilder) {
        int size = 100;
        SearchResponse response = this.transportClient.prepareSearch(index)
                .setTypes(type)
                .setSize(size)
                .setQuery(queryBuilder)
                .get();
        SearchHits searchHits = response.getHits();
        SearchHit[] searchHitArray = searchHits.getHits();
        List<T> tList = new ArrayList(searchHitArray.length);
        Map<String, Object> entityMap;
        T t;
        for (SearchHit searchHit : searchHitArray) {
            entityMap = searchHit.getSourceAsMap();
            t = this.parseMap(entityMap);
            tList.add(t);
        }
        return tList;
    }

    @Override
    public void delete(Object keyValue) {
        String id = this.getKeyValue(keyValue);
        this.transportClient.prepareDelete(index, type, id).get();
    }

    private Map<String, Object> getFieldMap(EsColumnHandler esColumnHandler) {
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
    public String check() {
        String result = "";
        //创建index
        IndicesExistsResponse indicesExistsResponse = this.transportClient.admin().indices().prepareExists(this.index).get();
        if (indicesExistsResponse.isExists() == false) {
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
        PutMappingResponse response = this.transportClient.admin().indices().preparePutMapping(index)
                .setType(this.type)
                .setSource(json, XContentType.JSON)
                .get();
        return result;
    }

}
