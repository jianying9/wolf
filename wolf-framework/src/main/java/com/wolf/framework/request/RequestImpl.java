package com.wolf.framework.request;

import com.wolf.framework.worker.context.WorkerContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public class RequestImpl implements WorkerRequest {

    private final WorkerContext workerContext;
    private final Map<String, Object> parameterMap;

    public RequestImpl(WorkerContext workerContext) {
        this.workerContext = workerContext;
        this.parameterMap = new HashMap(8, 1);
    }

    @Override
    public String getRoute() {
        return this.workerContext.getRoute();
    }

    @Override
    public String getSessionId() {
        return this.workerContext.getSessionId();
    }

    @Override
    public void removeSession() {
        this.workerContext.removeSession();
    }

    private Object getValue(String name) {
        return this.parameterMap.get(name);
    }

    @Override
    public void putParameter(String name, Object value) {
        this.parameterMap.put(name, value);
    }

    @Override
    public Long getLongValue(String name) {
        Object value = this.getValue(name);
        Long result = null;
        if (Long.class.isInstance(value)) {
            result = (Long) value;
        } else if (Integer.class.isInstance(value)) {
            result = ((Integer) value).longValue();
        } else if (String.class.isInstance(value)) {
            String s = (String) value;
            if (s.isEmpty()) {
                result = 0l;
            } else {
                result = Long.parseLong(s);
            }
        }
        return result;
    }

    @Override
    public Boolean getBooleanValue(String name) {
        Object value = this.getValue(name);
        Boolean result = null;
        if (Boolean.class.isInstance(value)) {
            result = (Boolean) value;
        } else if (String.class.isInstance(value)) {
            String s = (String) value;
            if (s.isEmpty()) {
                result = false;
            } else {
                result = Boolean.parseBoolean(s);
            }
        }
        return result;
    }

    @Override
    public Double getDoubleValue(String name) {
        Object value = this.getValue(name);
        Double result = null;
        if (Long.class.isInstance(value)) {
            Long l = (Long) value;
            result = l.doubleValue();
        } else if (Integer.class.isInstance(value)) {
            result = ((Integer) value).doubleValue();
        } else if (Double.class.isInstance(value)) {
            result = (Double) value;
        } else if (String.class.isInstance(value)) {
            String s = (String) value;
            if (s.isEmpty()) {
                result = 0d;
            } else {
                result = Double.parseDouble(s);
            }
        }
        return result;
    }

    @Override
    public String getStringValue(String name) {
        Object value = this.getValue(name);
        String v = null;
        if (String.class.isInstance(value)) {
            v = (String) value;
        } else if (Integer.class.isInstance(value)) {
            Integer i = (Integer) value;
            v = i.toString();
        } else if (Long.class.isInstance(value)) {
            Long l = (Long) value;
            v = l.toString();
        } else if (Boolean.class.isInstance(value)) {
            Boolean b = (Boolean) value;
            v = b.toString();
        } else if (Double.class.isInstance(value)) {
            Double d = (Double) value;
            v = d.toString();
        }
        return v;
    }

    @Override
    public List<Long> getLongListValue(String name) {
        Object value = this.getValue(name);
        return (List<Long>) value;
    }

    @Override
    public List<String> getStringListValue(String name) {
        Object value = this.getValue(name);
        return (List<String>) value;
    }

    @Override
    public Map<String, Object> getObjectValue(String name) {
        Object value = this.getValue(name);
        return (Map<String, Object>) value;
    }

    @Override
    public List<Map<String, Object>> getObjectListValue(String name) {
        Object value = this.getValue(name);
        return (List<Map<String, Object>>) value;
    }

    @Override
    public Long getLongValue(Map<String, Object> object, String name) {
        Object value = object.get(name);
        Long result = null;
        if (value != null) {
            if (Long.class.isInstance(value)) {
                result = (Long) value;
            } else if (Integer.class.isInstance(value)) {
                result = ((Integer) value).longValue();
            } else if (String.class.isInstance(value)) {
                String s = (String) value;
                if (s.isEmpty()) {
                    result = 0l;
                } else {
                    result = Long.parseLong(s);
                }
            }
        }
        return result;
    }

    @Override
    public Boolean getBooleanValue(Map<String, Object> object, String name) {
        Object value = object.get(name);
        Boolean result = null;
        if (Boolean.class.isInstance(value)) {
            result = (Boolean) value;
        } else if (String.class.isInstance(value)) {
            String s = (String) value;
            if (s.isEmpty()) {
                result = false;
            } else {
                result = Boolean.parseBoolean(s);
            }
        }
        return result;
    }

    @Override
    public Double getDoubleValue(Map<String, Object> object, String name) {
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
                String s = (String) value;
                if (s.isEmpty()) {
                    result = 0d;
                } else {
                    result = Double.parseDouble(s);
                }
            }
        }
        return result;
    }

    @Override
    public String getStringValue(Map<String, Object> object, String name) {
        Object value = object.get(name);
        return (String) value;
    }

    @Override
    public List<Long> getLongListValue(Map<String, Object> object, String name) {
        Object value = object.get(name);
        return (List<Long>) value;
    }

    @Override
    public List<String> getStringListValue(Map<String, Object> object, String name) {
        Object value = object.get(name);
        return (List<String>) value;
    }

    @Override
    public Map<String, Object> getObjectValue(Map<String, Object> object, String name) {
        Object value = object.get(name);
        return (Map<String, Object>) value;
    }

    @Override
    public List<Map<String, Object>> getObjectListValue(Map<String, Object> object, String name) {
        Object value = object.get(name);
        return (List<Map<String, Object>>) value;
    }

    @Override
    public String getIp() {
        return this.workerContext.getIp();
    }

}
