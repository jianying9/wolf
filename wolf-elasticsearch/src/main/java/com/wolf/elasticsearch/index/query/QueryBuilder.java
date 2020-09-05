package com.wolf.elasticsearch.index.query;

import java.util.Map;

/**
 *
 * @author jianying9
 */
public interface QueryBuilder
{
    public Map<String, Object> toMap();
}
