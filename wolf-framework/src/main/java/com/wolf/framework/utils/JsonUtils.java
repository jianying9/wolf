package com.wolf.framework.utils;

import com.wolf.framework.service.parameter.OutputParameterHandler;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author aladdin
 */
public final class JsonUtils {

    private JsonUtils() {
    }
    
    public static String listToJSON(List<String> list) {
        String result = "[]";
        if(list.isEmpty() == false) {
            StringBuilder jsonBuilder = new StringBuilder(list.size() * 36);
            jsonBuilder.append('[');
            for (String text : list) {
                jsonBuilder.append('"').append(text).append("\",");
            }
            jsonBuilder.setLength(jsonBuilder.length() - 1);
            jsonBuilder.append(']');
            result = jsonBuilder.toString();
        }
        return result;
    }
    
    public static String setToJSON(Set<String> list) {
        String result = "[]";
        if(list.isEmpty() == false) {
            StringBuilder jsonBuilder = new StringBuilder(list.size() * 36);
            jsonBuilder.append('[');
            for (String text : list) {
                jsonBuilder.append('"').append(text).append("\",");
            }
            jsonBuilder.setLength(jsonBuilder.length() - 1);
            jsonBuilder.append(']');
            result = jsonBuilder.toString();
        }
        return result;
    }

    public static String valueToJSON(final String value, final OutputParameterHandler fieldHandler) {
        StringBuilder jsonBuilder = new StringBuilder(32);
        jsonBuilder.append('{').append(fieldHandler.getJson(value)).append('}');
        String jsonStr = jsonBuilder.toString();
        return jsonStr;
    }

    public static String valuesToJSON(final String[] values, final OutputParameterHandler fieldHandler) {
        String jsonStr = "{}";//return
        if (values != null) {
            StringBuilder jsonBuilder = new StringBuilder(values.length * 32);
            for (String value : values) {
                jsonBuilder.append('{').append(fieldHandler.getJson(value)).append("},");
            }
            jsonBuilder.setLength(jsonBuilder.length() - 1);
            jsonStr = jsonBuilder.toString();
        }
        return jsonStr;
    }

    public static String mapToJSON(final Map<String, String> parameterMap, final String[] fieldNames, final Map<String, OutputParameterHandler> fieldMap) {
        String jsonStr = "{}";//return
        if (parameterMap != null) {
            StringBuilder jsonBuilder = new StringBuilder(fieldNames.length * 32);
            JsonUtils.mapToJSON(parameterMap, fieldNames, fieldMap, jsonBuilder);
            jsonStr = jsonBuilder.toString();
        }
        return jsonStr;
    }

    public static String mapListToJSON(List<Map<String, String>> parameterMapList, String[] fieldNames, Map<String, OutputParameterHandler> fieldMap) {
        String jsonStr = "";//return
        if (parameterMapList != null && !parameterMapList.isEmpty()) {
            if (parameterMapList.size() == 1) {
                jsonStr = JsonUtils.mapToJSON(parameterMapList.get(0), fieldNames, fieldMap);
            } else {
                StringBuilder jsonBuilder = new StringBuilder(parameterMapList.size() * fieldNames.length * 32);
                for (Map<String, String> parameterMap : parameterMapList) {
                    JsonUtils.mapToJSON(parameterMap, fieldNames, fieldMap, jsonBuilder);
                    jsonBuilder.append(',');
                }
                jsonBuilder.setLength(jsonBuilder.length() - 1);
                jsonStr = jsonBuilder.toString();
            }
        }
        return jsonStr;
    }

    private static void mapToJSON(final Map<String, String> parameterMap, final String[] fieldNames, final Map<String, OutputParameterHandler> fieldMap, final StringBuilder jsonBuilder) {
        String value;
        OutputParameterHandler fieldHandler;
        boolean isExist = false;
        jsonBuilder.append('{');
        for (String fieldName : fieldNames) {
            value = parameterMap.get(fieldName);
            if (value != null) {
                fieldHandler = fieldMap.get(fieldName);
                if (fieldHandler != null) {
                    jsonBuilder.append(fieldHandler.getJson(value)).append(',');
                    isExist = true;
                }
            }
        }
        if (isExist) {
            jsonBuilder.setLength(jsonBuilder.length() - 1);
        }
        jsonBuilder.append('}');
    }
}
