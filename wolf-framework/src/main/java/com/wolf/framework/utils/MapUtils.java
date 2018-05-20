package com.wolf.framework.utils;

import java.util.List;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public final class MapUtils {

    public static Long getLongValue(Map<String, Object> object, String name) {
        Object value = object.get(name);
        Long result = null;
        if (value != null) {
            if (Long.class.isInstance(value)) {
                result = (Long) value;
            } else if (Integer.class.isInstance(value)) {
                result = ((Integer) value).longValue();
            } else if (String.class.isInstance(value)) {
                result = Long.parseLong((String) value);
            }
        }
        return result;
    }

    public static Boolean getBooleanValue(Map<String, Object> object, String name) {
        Object value = object.get(name);
        Boolean result = null;
        if (Boolean.class.isInstance(value)) {
            result = (Boolean) value;
        } else if (String.class.isInstance(value)) {
            result = Boolean.parseBoolean((String) value);
        }
        return result;
    }

    public static Double getDoubleValue(Map<String, Object> object, String name) {
        Object value = object.get(name);
        Double result = null;
        if (value != null) {
            if (Double.class.isInstance(value)) {
                result = (Double) value;
            } else if (Long.class.isInstance(value)) {
                result = ((Long) value).doubleValue();
            } else if (Integer.class.isInstance(value)) {
                result = ((Integer) value).doubleValue();
            } else if (String.class.isInstance(value)) {
                result = Double.parseDouble((String) value);
            }
        }
        return result;
    }

    public static String getStringValue(Map<String, Object> object, String name) {
        String result = null;
        Object value = object.get(name);
        if (value != null && String.class.isInstance(value)) {
            result = (String) value;
        }
        return result;
    }

    public static List<Long> getLongListValue(Map<String, Object> object, String name) {
        Object value = object.get(name);
        return (List<Long>) value;
    }

    public static List<String> getStringListValue(Map<String, Object> object, String name) {
        Object value = object.get(name);
        return (List<String>) value;
    }

    public static Map<String, Object> getObjectValue(Map<String, Object> object, String name) {
        Object value = object.get(name);
        return (Map<String, Object>) value;
    }

    public static List<Map<String, Object>> getObjectListValue(Map<String, Object> object, String name) {
        Object value = object.get(name);
        return (List<Map<String, Object>>) value;
    }
}
