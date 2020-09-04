package com.wolf.elasticsearch.search.sort;

import com.wolf.elasticsearch.script.Script;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public class ScriptSortBuilder implements SortBuilder
{

    private final Script script;

    private final ScriptSortType type;

    private SortOrder order = SortOrder.ASC;

    public ScriptSortBuilder(Script script, ScriptSortType type)
    {
        this.script = script;
        this.type = type;
    }

    public ScriptSortBuilder order(SortOrder order)
    {
        this.order = order;
        return this;
    }

    @Override
    public Map<String, Object> toMap()
    {
        Map<String, Object> valueMap = this.script.toMap();
        valueMap.put("order", order.toString());
        valueMap.put("type", this.type.toString());

        //
        Map<String, Object> fieldMap = new HashMap();
        fieldMap.put("_script", valueMap);
        return fieldMap;
    }

    public enum ScriptSortType
    {
        STRING,
        NUMBER;

        @Override
        public String toString()
        {
            return name().toLowerCase();
        }
    }

}
