package com.tm.core.util.converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlParamsConverter implements ISqlParamsConverter {
    public Map<Integer, String> getStringParamsMap(List<String> params) {
        return new HashMap<>() {{
            for (int i = 0; i < params.size(); i++) {
                put(i, params.get(i));
            }
        }};
    }

    public Map<Integer, Integer> getIntegerParamsMap(List<Integer> params) {
        return new HashMap<>() {{
            for (int i = 0; i < params.size(); i++) {
                put(i, params.get(i));
            }
        }};
    }

    public Map<Integer, Object> getObjectParamsMap(List<Object> params) {
        return new HashMap<>() {{
            int index = 1;
            for (Object param : params) {
                put(index++, param);
            }
        }};
    }

    @Override
    public Map<Integer, String> getObjectParamsMap(String param) {
        return new HashMap<>() {{
            put(1, param);
        }};
    }

    @Override
    public Map<Integer, Number> getObjectParamsMap(Number param) {
        return new HashMap<>() {{
            put(1, param);
        }};
    }
}
