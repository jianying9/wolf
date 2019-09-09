package com.wolf.framework.dao.elasticsearch;

import com.wolf.framework.dao.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilder;

/**
 *
 * @author jianying9
 * @param <T>
 */
public class EsEntityDaoImpl<T extends Entity> extends AbstractEsEntityDao<T> implements EsEntityDao<T> {

    public EsEntityDaoImpl(
            TransportClient transportClient,
            String index,
            String type,
            EsColumnHandler keyHandler,
            List<EsColumnHandler> columnHandlerList,
            Class<T> clazz) {
        super(transportClient, index, type, keyHandler, columnHandlerList, clazz);
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
     * @param sort
     * @param from
     * @param size
     * @return
     */
    @Override
    public List<T> search(QueryBuilder queryBuilder, SortBuilder sort, int from, int size) {
        SearchRequestBuilder searchRequestBuilder = this.transportClient.prepareSearch(index)
                .setTypes(type)
                .setFrom(from)
                .setSize(size)
                .setVersion(false);
        if (queryBuilder != null) {
            searchRequestBuilder.setQuery(queryBuilder);
        }
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

    @Override
    public SearchResponse searcResponse(QueryBuilder queryBuilder, int from, int size) {
        SearchRequestBuilder searchRequestBuilder = this.transportClient.prepareSearch(index)
                .setTypes(type)
                .setFrom(from)
                .setSize(size)
                .setVersion(false);
        if (queryBuilder != null) {
            searchRequestBuilder.setQuery(queryBuilder);
        }
        return searchRequestBuilder.get();
    }

    @Override
    public List<T> search(QueryBuilder queryBuilder, List<SortBuilder> sortList, int from, int size) {
        SearchRequestBuilder searchRequestBuilder = this.transportClient.prepareSearch(index)
                .setTypes(type)
                .setFrom(from)
                .setSize(size)
                .setVersion(false);
        if (queryBuilder != null) {
            searchRequestBuilder.setQuery(queryBuilder);
        }
        if (sortList != null) {
            for (SortBuilder sortBuilder : sortList) {
                searchRequestBuilder.addSort(sortBuilder);
            }
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

}
