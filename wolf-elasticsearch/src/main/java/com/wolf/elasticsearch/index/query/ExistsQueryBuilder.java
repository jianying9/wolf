package com.wolf.elasticsearch.index.query;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public class ExistsQueryBuilder implements QueryBuilder
{

    private final String fieldName;

    public ExistsQueryBuilder(String fieldName)
    {
        this.fieldName = fieldName;
    }

    @Override
    public Map<String, Object> toMap()
    {
        Map<String, Object> fieldMap = new HashMap();
        fieldMap.put("field", this.fieldName);
        //
        Map<String, Object> termMap = new HashMap();
        termMap.put("exists", fieldMap);
        return termMap;
    }

}
