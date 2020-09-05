package com.wolf.elasticsearch.index.query;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public class TermsQueryBuilder implements QueryBuilder
{

    private final String fieldName;

    private final Collection<Object> values;

    public TermsQueryBuilder(String fieldName, Collection<Object> values)
    {
        this.fieldName = fieldName;
        this.values = values;
    }

    @Override
    public Map<String, Object> toMap()
    {
        Map<String, Object> fieldMap = new HashMap();
        fieldMap.put(fieldName, values);
        //
        Map<String, Object> termMap = new HashMap();
        termMap.put("terms", fieldMap);
        return termMap;
    }

}
