package com.wolf.elasticsearch.search.sort;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public class FieldSortBuilder implements SortBuilder
{

    private final String fieldName;

    private SortOrder order = SortOrder.ASC;

    public FieldSortBuilder(String fieldName)
    {
        this.fieldName = fieldName;
    }

    public FieldSortBuilder order(SortOrder order)
    {
        this.order = order;
        return this;
    }

    @Override
    public Map<String, Object> toMap()
    {
        Map<String, Object> orderMap = new HashMap();
        orderMap.put("order", order.toString());
        //
        Map<String, Object> fieldMap = new HashMap();
        fieldMap.put(this.fieldName, orderMap);
        return fieldMap;
    }

}
