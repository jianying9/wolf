package com.wolf.elasticsearch.index.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public class BoolQueryBuilder implements QueryBuilder
{

    private final List<QueryBuilder> mustClauses = new ArrayList();

    private final List<QueryBuilder> mustNotClauses = new ArrayList();
    
    private final List<QueryBuilder> shouldClauses = new ArrayList();

    public BoolQueryBuilder must(QueryBuilder queryBuilder)
    {
        mustClauses.add(queryBuilder);
        return this;
    }

    public BoolQueryBuilder mustNot(QueryBuilder queryBuilder)
    {
        mustNotClauses.add(queryBuilder);
        return this;
    }
    
    public BoolQueryBuilder should(QueryBuilder queryBuilder)
    {
        shouldClauses.add(queryBuilder);
        return this;
    }

    @Override
    public Map<String, Object> toMap()
    {
        List<Map<String, Object>> mustMapList = new ArrayList();
        for (QueryBuilder qb : mustClauses) {
            mustMapList.add(qb.toMap());
        }
        //
        List<Map<String, Object>> mustNotMapList = new ArrayList();
        for (QueryBuilder qb : mustNotClauses) {
            mustNotMapList.add(qb.toMap());
        }
        //
        List<Map<String, Object>> shouldMapList = new ArrayList();
        for (QueryBuilder qb : shouldClauses) {
            shouldMapList.add(qb.toMap());
        }
        //
        Map<String, Object> fieldMap = new HashMap();
        if (mustMapList.isEmpty() == false) {
            fieldMap.put("must", mustMapList);
        }
        if (mustNotMapList.isEmpty() == false) {
            fieldMap.put("must_not", mustNotMapList);
        }
        if (shouldMapList.isEmpty() == false) {
            fieldMap.put("should", shouldMapList);
        }
        //
        Map<String, Object> boolMap = new HashMap();
        boolMap.put("bool", fieldMap);
        return boolMap;
    }
}
