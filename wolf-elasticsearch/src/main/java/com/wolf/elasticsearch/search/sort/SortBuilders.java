package com.wolf.elasticsearch.search.sort;

import com.wolf.elasticsearch.script.Script;
import com.wolf.elasticsearch.search.sort.ScriptSortBuilder.ScriptSortType;

/**
 *
 * @author jianying9
 */
public class SortBuilders
{
    public static FieldSortBuilder fieldSort(String field) {
        return new FieldSortBuilder(field);
    }
    
    public static ScriptSortBuilder scriptSort(Script script, ScriptSortType type) {
        return new ScriptSortBuilder(script, type);
    }
}
