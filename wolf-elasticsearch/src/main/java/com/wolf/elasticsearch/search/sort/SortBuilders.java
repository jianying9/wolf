package com.wolf.elasticsearch.search.sort;

/**
 *
 * @author jianying9
 */
public class SortBuilders
{
    public static FieldSortBuilder fieldSort(String field) {
        return new FieldSortBuilder(field);
    }
}
