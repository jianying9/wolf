package com.wolf.framework.dao.elasticsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wolf.framework.dao.Entity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.http.util.EntityUtils;
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
public class EsEntityDaoImpl<T extends Entity> extends AbstractEsEntityDao<T> implements EsEntityDao<T>
{

    public EsEntityDaoImpl(
            RestClient restClient,
            String index,
            String type,
            EsColumnHandler keyHandler,
            List<EsColumnHandler> columnHandlerList,
            Class<T> clazz)
    {
        super(restClient, index, type, keyHandler, columnHandlerList, clazz);
    }

    @Override
    public long total()
    {
        long result = 0;
        String path = "/" + index + "/" + type + "/_search";
        Map<String, Object> requestMap = new HashMap();
        requestMap.put("from", 0);
        requestMap.put("size", 1);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Request request = new Request("POST", path);
            String searchJson = objectMapper.writeValueAsString(requestMap);
            request.setJsonEntity(searchJson);
            Response response = restClient.performRequest(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
            Map<String, Object> hitsMap = (Map<String, Object>) responseMap.get("hits");
            result = (Integer) hitsMap.get("total");
        } catch (IOException ex) {
        }
        return result;
    }

    @Override
    public String insert(Map<String, Object> entityMap)
    {
        Object keyValue = entityMap.get(this.keyHandler.getColumnName());
        if (keyValue == null) {
            throw new RuntimeException("Can not find keyValue when insert:" + entityMap.toString());
        }
        //删除多余的列名
        Set<String> keySet = new HashSet();
        keySet.addAll(entityMap.keySet());
        for (String columnName : keySet) {
            if (this.allColumnNameSet.contains(columnName) == false) {
                entityMap.remove(columnName);
            }
        }
        String id = this.getKeyValue(keyValue);
        String path = "/" + index + "/" + type + "/" + id + "?refresh=true";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Request request = new Request("PUT", path);
            String jsonStr = objectMapper.writeValueAsString(entityMap);
            request.setJsonEntity(jsonStr);
            restClient.performRequest(request);
        } catch (IOException ex) {
        }
        return id;
    }

    @Override
    public String update(Map<String, Object> entityMap)
    {
        Object keyValue = entityMap.get(this.keyHandler.getColumnName());
        if (keyValue == null) {
            throw new RuntimeException("Can not find keyValue when update:" + entityMap.toString());
        }
        //删除多余的列名
        Set<String> keySet = new HashSet();
        keySet.addAll(entityMap.keySet());
        for (String columnName : keySet) {
            if (this.allColumnNameSet.contains(columnName) == false) {
                entityMap.remove(columnName);
            }
        }
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> postMap = new HashMap();
        postMap.put("doc", entityMap);
        String id = this.getKeyValue(keyValue);
        String path = "/" + index + "/" + type + "/" + id + "/_update?refresh=true";
        try {
            Request request = new Request("POST", path);
            String jsonStr = objectMapper.writeValueAsString(postMap);
            request.setJsonEntity(jsonStr);
            restClient.performRequest(request);
        } catch (IOException ex) {
        }
        //
        return id;
    }

    @Override
    public String upsert(Map<String, Object> entityMap)
    {
        Object keyValue = entityMap.get(this.keyHandler.getColumnName());
        if (keyValue == null) {
            throw new RuntimeException("Can not find keyValue when update:" + entityMap.toString());
        }
        //删除多余的列名
        Set<String> keySet = new HashSet();
        keySet.addAll(entityMap.keySet());
        for (String columnName : keySet) {
            if (this.allColumnNameSet.contains(columnName) == false) {
                entityMap.remove(columnName);
            }
        }
        String id = this.getKeyValue(keyValue);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> postMap = new HashMap();
        postMap.put("doc", entityMap);
        postMap.put("doc_as_upsert", true);
        String path = "/" + index + "/" + type + "/" + id + "/_update?refresh=true";
        try {
            Request request = new Request("POST", path);
            String jsonStr = objectMapper.writeValueAsString(postMap);
            request.setJsonEntity(jsonStr);
            restClient.performRequest(request);
        } catch (IOException ex) {
        }
        return id;
    }

    @Override
    public T inquireByKey(Object keyValue)
    {
        T t = null;
        String id = this.getKeyValue(keyValue);
        String path = "/" + index + "/" + type + "/" + id;
        try {
            Request request = new Request("GET", path);
            Response response = restClient.performRequest(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
            Map<String, Object> entityMap = (Map<String, Object>) responseMap.get("_source");
            t = this.parseMap(entityMap);
        } catch (IOException ex) {
        }
        return t;
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
    public List<T> search(QueryBuilder queryBuilder, SortBuilder sort, int from, int size)
    {
        List<T> tList = Collections.EMPTY_LIST;
        String path = "/" + index + "/" + type + "/_search";
        Map<String, Object> requestMap = new HashMap();
        if (queryBuilder != null) {
            requestMap.put("query", queryBuilder.toMap());
        }
        requestMap.put("from", from);
        requestMap.put("size", size);
        if (sort != null) {
            List<Map<String, Object>> sortMapList = new ArrayList();
            sortMapList.add(sort.toMap());
            requestMap.put("sort", sortMapList);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Request request = new Request("POST", path);
            String searchJson = objectMapper.writeValueAsString(requestMap);
            request.setJsonEntity(searchJson);
            Response response = restClient.performRequest(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
            Map<String, Object> hitsMap = (Map<String, Object>) responseMap.get("hits");
            List<Map<String, Object>> hitsMapList = (List<Map<String, Object>>) hitsMap.get("hits");
            tList = new ArrayList(hitsMapList.size());
            T t;
            Map<String, Object> entityMap;
            for (Map<String, Object> dataMap : hitsMapList) {
                entityMap = (Map<String, Object>) dataMap.get("_source");
                t = this.parseMap(entityMap);
                tList.add(t);
            }
        } catch (IOException ex) {
        }
        return tList;
    }

    @Override
    public List<T> search(QueryBuilder queryBuilder, List<SortBuilder> sortList, int from, int size)
    {
        List<T> tList = Collections.EMPTY_LIST;
        String path = "/" + index + "/" + type + "/_search";
        Map<String, Object> requestMap = new HashMap();
        if (queryBuilder != null) {
            requestMap.put("query", queryBuilder.toMap());
        }
        requestMap.put("from", from);
        requestMap.put("size", size);
        if (sortList != null && sortList.isEmpty() == false) {
            List<Map<String, Object>> sortMapList = new ArrayList();
            for (SortBuilder sortBuilder : sortList) {
                sortMapList.add(sortBuilder.toMap());
            }
            requestMap.put("sort", sortMapList);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Request request = new Request("POST", path);
            String searchJson = objectMapper.writeValueAsString(requestMap);
            request.setJsonEntity(searchJson);
            Response response = restClient.performRequest(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
            Map<String, Object> hitsMap = (Map<String, Object>) responseMap.get("hits");
            List<Map<String, Object>> hitsMapList = (List<Map<String, Object>>) hitsMap.get("hits");
            tList = new ArrayList(hitsMapList.size());
            T t;
            Map<String, Object> entityMap;
            for (Map<String, Object> dataMap : hitsMapList) {
                entityMap = (Map<String, Object>) dataMap.get("_source");
                t = this.parseMap(entityMap);
                tList.add(t);
            }
        } catch (IOException ex) {
        }
        return tList;
    }

}
