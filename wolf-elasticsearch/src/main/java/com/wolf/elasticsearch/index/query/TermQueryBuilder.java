package com.wolf.elasticsearch.index.query;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public class TermQueryBuilder implements QueryBuilder
{

    private final String fieldName;

    private final Object value;

    public TermQueryBuilder(String fieldName, Object value)
    {
        this.fieldName = fieldName;
        this.value = value;
    }

    @Override
    public Map<String, Object> toMap()
    {
        Map<String, Object> fieldMap = new HashMap();
        fieldMap.put(fieldName, value);
        //
        Map<String, Object> termMap = new HashMap();
        termMap.put("term", fieldMap);
        return termMap;
    }

}
