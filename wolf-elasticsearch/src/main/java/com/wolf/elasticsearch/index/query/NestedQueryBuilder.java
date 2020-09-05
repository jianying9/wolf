package com.wolf.elasticsearch.index.query;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public class NestedQueryBuilder implements QueryBuilder
{

    private final String path;

    private final QueryBuilder queryBuilder;

    public NestedQueryBuilder(String path, QueryBuilder queryBuilder)
    {
        this.path = path;
        this.queryBuilder = queryBuilder;
    }

    @Override
    public Map<String, Object> toMap()
    {
        Map<String, Object> fieldMap = new HashMap();
        fieldMap.put("path", this.path);
        fieldMap.put("score_mode", "none");
        fieldMap.put("query", this.queryBuilder.toMap());
        //
        Map<String, Object> termMap = new HashMap();
        termMap.put("nested", fieldMap);
        return termMap;
    }

}
