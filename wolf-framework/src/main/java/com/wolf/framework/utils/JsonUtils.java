package com.wolf.framework.utils;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.service.parameter.ResponseDataType;
import com.wolf.framework.service.parameter.ResponseParameterHandler;
import com.wolf.framework.service.parameter.filter.Filter;
import com.wolf.framework.service.parameter.response.BooleanResponseParameterHandlerImpl;
import com.wolf.framework.service.parameter.response.NumberResponseParameterHandlerImpl;
import com.wolf.framework.service.parameter.response.StringResponseParameterHandlerImpl;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author aladdin
 */
public final class JsonUtils {

    private static final Map<Class<?>, Map<String, ResponseParameterHandler>> CONFIG_MAP = new HashMap<>();

    private JsonUtils() {
    }

    public static String listToJSON(List<String> list) {
        String result = "[]";
        if (list.isEmpty() == false) {
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
        if (list.isEmpty() == false) {
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

    public static String valueToJSON(final String value, final ResponseParameterHandler fieldHandler) {
        StringBuilder jsonBuilder = new StringBuilder(32);
        jsonBuilder.append('{').append(fieldHandler.getJson(value)).append('}');
        String jsonStr = jsonBuilder.toString();
        return jsonStr;
    }

    public static String valuesToJSON(final String[] values, final ResponseParameterHandler fieldHandler) {
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

    public static String mapToJSON(final Map<String, String> parameterMap, final String[] fieldNames, final Map<String, ResponseParameterHandler> fieldMap) {
        String jsonStr = "{}";//return
        if (parameterMap != null) {
            StringBuilder jsonBuilder = new StringBuilder(fieldNames.length * 32);
            JsonUtils.mapToJSON(parameterMap, fieldNames, fieldMap, jsonBuilder);
            jsonStr = jsonBuilder.toString();
        }
        return jsonStr;
    }

    private static Map<String, ResponseParameterHandler> getResponseParameterMap(Class<?> clazz) {
        Map<String, ResponseParameterHandler> map = CONFIG_MAP.get(clazz);
        if (map == null) {
            map = new HashMap<>(2, 1);
            //解析
            Field[] fieldTemp = clazz.getDeclaredFields();
            Filter[] filters = ApplicationContext.CONTEXT.getFilterFactory().getAllFilter();
            int modifier;
            String fieldName;
            String type;
            ResponseParameterHandler handler;
            for (Field field : fieldTemp) {
                modifier = field.getModifiers();
                if (Modifier.isStatic(modifier) == false) {
                    fieldName = field.getName();
                    type = field.getType().getName();
                    switch (type) {
                        case "long":
                        case "java.lang.Long":
                        case "int":
                        case "java.lang.Integer":
                            handler = new NumberResponseParameterHandlerImpl(fieldName, ResponseDataType.LONG);
                            break;
                        case "double":
                        case "java.lang.Double":
                            handler = new NumberResponseParameterHandlerImpl(fieldName, ResponseDataType.DOUBLE);
                            break;
                        case "boolean":
                        case "java.lang.Boolean":
                            handler = new BooleanResponseParameterHandlerImpl(fieldName);
                            break;
                        default:
                            handler = new StringResponseParameterHandlerImpl(fieldName, filters);
                            break;
                    }
                    map.put(handler.getName(), handler);
                }
            }
            CONFIG_MAP.put(clazz, map);
        }
        return map;
    }

    public static String mapListToJSON(List<Map<String, String>> parameterMapList, Class<?> clazz) {
        Map<String, ResponseParameterHandler> map = getResponseParameterMap(clazz);
        String[] fieldNames = map.keySet().toArray(new String[map.size()]);
        return mapListToJSON(parameterMapList, fieldNames, map);
    }

    public static String mapListToJSON(List<Map<String, String>> parameterMapList, String[] fieldNames, Map<String, ResponseParameterHandler> fieldMap) {
        String jsonStr = "[]";//return
        if (parameterMapList != null && !parameterMapList.isEmpty()) {
            StringBuilder jsonBuilder = new StringBuilder(parameterMapList.size() * fieldNames.length * 32);
            jsonBuilder.append("[");
            if (parameterMapList.size() == 1) {
                jsonStr = JsonUtils.mapToJSON(parameterMapList.get(0), fieldNames, fieldMap);
                jsonBuilder.append(jsonStr);
            } else {
                for (Map<String, String> parameterMap : parameterMapList) {
                    JsonUtils.mapToJSON(parameterMap, fieldNames, fieldMap, jsonBuilder);
                    jsonBuilder.append(',');
                }
                jsonBuilder.setLength(jsonBuilder.length() - 1);
            }
            jsonBuilder.append("]");
            jsonStr = jsonBuilder.toString();
        }
        return jsonStr;
    }

    private static void mapToJSON(final Map<String, String> parameterMap, final String[] fieldNames, final Map<String, ResponseParameterHandler> fieldMap, final StringBuilder jsonBuilder) {
        String value;
        ResponseParameterHandler fieldHandler;
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
