package com.wolf.elasticsearch.script;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public class Script
{

    private final String lang = "painless";

    private final String source;

    private final Map<String, Object> paramsMap;

    public Script(String source)
    {
        this.source = source;
        this.paramsMap = Collections.EMPTY_MAP;
    }

    public Script(String source, Map<String, Object> paramsMap)
    {
        this.source = source;
        this.paramsMap = paramsMap;
    }

    public Map<String, Object> toMap()
    {
        Map<String, Object> valueMap = new HashMap();
        valueMap.put("lang", this.lang);
        valueMap.put("source", this.source);
        valueMap.put("params", this.paramsMap);
        Map<String, Object> scriptMap = new HashMap();
        scriptMap.put("script", valueMap);
        return scriptMap;
    }

}
