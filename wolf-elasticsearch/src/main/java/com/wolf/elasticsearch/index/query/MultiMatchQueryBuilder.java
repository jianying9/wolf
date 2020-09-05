package com.wolf.elasticsearch.index.query;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public class MultiMatchQueryBuilder implements QueryBuilder
{

    private final String[] fieldNames;

    private final String text;

    public MultiMatchQueryBuilder(String text, String... fieldNames)
    {
        this.fieldNames = fieldNames;
        this.text = text;
    }

    @Override
    public Map<String, Object> toMap()
    {
        Map<String, Object> fieldMap = new HashMap();
        fieldMap.put("query", text);
        fieldMap.put("fields", this.fieldNames);
        //
        Map<String, Object> matchMap = new HashMap();
        matchMap.put("multi_match", fieldMap);
        return matchMap;
    }
}
