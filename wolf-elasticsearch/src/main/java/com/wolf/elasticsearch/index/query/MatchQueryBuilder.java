package com.wolf.elasticsearch.index.query;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public class MatchQueryBuilder implements QueryBuilder
{

    private final String fieldName;

    private final String text;

    public MatchQueryBuilder(String fieldName, String text)
    {
        this.fieldName = fieldName;
        this.text = text;
    }

    @Override
    public Map<String, Object> toMap()
    {
        Map<String, Object> fieldMap = new HashMap();
        fieldMap.put(fieldName, text);
        //
        Map<String, Object> matchMap = new HashMap();
        matchMap.put("match", fieldMap);
        return matchMap;
    }
}
