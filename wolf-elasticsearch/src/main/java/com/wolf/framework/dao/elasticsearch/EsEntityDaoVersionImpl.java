package com.wolf.framework.dao.elasticsearch;

import com.wolf.framework.dao.Entity;
import com.wolf.framework.exception.ResponseCodeException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.engine.VersionConflictEngineException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilder;

/**
 *
 * @author jianying9
 * @param <T>
 */
public class EsEntityDaoVersionImpl<T extends Entity> extends AbstractEsEntityDao<T> implements EsEntityDao<T> {

    protected final EsColumnHandler versionHandler;

    public EsEntityDaoVersionImpl(
            TransportClient transportClient,
            String index,
            String type,
            EsColumnHandler keyHandler,
            List<EsColumnHandler> columnHandlerList,
            EsColumnHandler versionHandler,
            Class<T> clazz) {
        super(transportClient, index, type, keyHandler, columnHandlerList, clazz);
        this.versionHandler = versionHandler;
    }

    @Override
    public String insert(Map<String, Object> entityMap) {
        Object keyValue = entityMap.get(this.keyHandler.getColumnName());
        if (keyValue == null) {
            throw new RuntimeException("Can not find keyValue when insert:" + entityMap.toString());
        }
        //移除version
        entityMap.remove(this.versionHandler.getColumnName());
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
            //移除version
            entityMap.remove(this.versionHandler.getColumnName());
            id = this.getKeyValue(keyValue);
            indexRequest = this.transportClient.prepareIndex(index, type, id).setSource(entityMap).request();
            bulkRequestBuilder.add(indexRequest);
        }
        bulkRequestBuilder.get();
        //
        this.refresh();
    }

    private long parseVersion(Object value) {
        long result = 0;
        if (Long.class.isInstance(value)) {
            result = (Long) value;
        } else if (Integer.class.isInstance(value)) {
            result = ((Integer) value).longValue();
        } else if (String.class.isInstance(value)) {
            result = Long.parseLong((String) value);
        }
        return result;
    }

    @Override
    public String update(Map<String, Object> entityMap) {
        Object keyValue = entityMap.get(this.keyHandler.getColumnName());
        if (keyValue == null) {
            throw new RuntimeException("Can not find keyValue when update:" + entityMap.toString());
        }
        Object versionValue = entityMap.get(this.versionHandler.getColumnName());
        if (versionValue == null) {
            throw new RuntimeException("Can not find versionValue when update:" + entityMap.toString());
        }
        entityMap.remove(this.versionHandler.getColumnName());
        long version = this.parseVersion(versionValue);
        String id = this.getKeyValue(keyValue);
        UpdateRequestBuilder updateRequestBuilder = this.transportClient.prepareUpdate(index, type, id)
                .setDoc(entityMap)
                .setVersion(version);
        try {
            updateRequestBuilder.get();
        } catch (VersionConflictEngineException e) {
            logger.error("elasticsearch version exception", e);
            ResponseCodeException rce = new ResponseCodeException("data_expire");
            rce.setDesc("数据已过期,请刷新");
            throw rce;
        }
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
        Object versionValue;
        long version;
        for (Map<String, Object> entityMap : entityMapList) {
            keyValue = entityMap.get(this.keyHandler.getColumnName());
            if (keyValue == null) {
                throw new RuntimeException("Can not find keyValue when update:" + entityMap.toString());
            }
            versionValue = entityMap.get(this.versionHandler.getColumnName());
            if (versionValue == null) {
                throw new RuntimeException("Can not find versionValue when update:" + entityMap.toString());
            }
            entityMap.remove(this.versionHandler.getColumnName());
            version = this.parseVersion(versionValue);
            id = this.getKeyValue(keyValue);
            updateRequest = this.transportClient.prepareUpdate(index, type, id)
                    .setDoc(entityMap)
                    .setVersion(version)
                    .request();

            bulkRequestBuilder.add(updateRequest);
        }
        try {
            bulkRequestBuilder.get();
        } catch (VersionConflictEngineException e) {
            logger.error("elasticsearch version exception", e);
            ResponseCodeException rce = new ResponseCodeException("data_expire");
            rce.setDesc("数据已过期,请刷新");
            throw rce;
        }
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
            //读取version
            entityMap.put(this.versionHandler.getColumnName(), getResponse.getVersion());
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
                .setVersion(true);
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
            //读取version
            entityMap.put(this.versionHandler.getColumnName(), searchHit.getVersion());
            t = this.parseMap(entityMap);
            tList.add(t);
        }
        return tList;
    }

    @Override
    public List<T> search(QueryBuilder queryBuilder, List<SortBuilder> sortList, int from, int size) {
        SearchRequestBuilder searchRequestBuilder = this.transportClient.prepareSearch(index)
                .setTypes(type)
                .setFrom(from)
                .setSize(size)
                .setVersion(true);
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
            //读取version
            entityMap.put(this.versionHandler.getColumnName(), searchHit.getVersion());
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
                .setVersion(true);
        if (queryBuilder != null) {
            searchRequestBuilder.setQuery(queryBuilder);
        }
        return searchRequestBuilder.get();
    }

}
