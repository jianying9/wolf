package com.wolf.elasticsearch.index.query;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public class RangeQueryBuilder implements QueryBuilder
{

    private final String fieldName;
    private Object minValue = null;
    private Object maxValue = null;
    private boolean gte = false;
    private boolean gt = false;
    private boolean lte = false;
    private boolean lt = false;

    public RangeQueryBuilder(String fieldName)
    {
        this.fieldName = fieldName;
    }

    public RangeQueryBuilder gt(Object from)
    {
        this.gt = true;
        this.gte = false;
        this.minValue = from;
        return this;
    }

    public RangeQueryBuilder gte(Object from)
    {
        this.gt = false;
        this.gte = true;
        this.minValue = from;
        return this;
    }

    public RangeQueryBuilder lt(Object to)
    {
        this.lt = true;
        this.lte = false;
        this.maxValue = to;
        return this;
    }

    public RangeQueryBuilder lte(Object to)
    {
        this.lt = false;
        this.lte = true;
        this.minValue = to;
        return this;
    }

    @Override
    public Map<String, Object> toMap()
    {
        Map<String, Object> valueMap = new HashMap();
        if (this.gt) {
            valueMap.put("gt", minValue);
        }
        if (this.gte) {
            valueMap.put("gte", minValue);
        }
        if (this.lt) {
            valueMap.put("lt", maxValue);
        }
        if (this.lte) {
            valueMap.put("lte", maxValue);
        }
        //
        Map<String, Object> fieldMap = new HashMap();
        fieldMap.put(fieldName, valueMap);
        //
        Map<String, Object> rangeMap = new HashMap();
        rangeMap.put("range", fieldMap);
        return rangeMap;
    }
}
