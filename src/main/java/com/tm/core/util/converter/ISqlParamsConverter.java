package com.tm.core.util.converter;

import java.util.List;
import java.util.Map;

public interface ISqlParamsConverter {
    Map<Integer, Object> getObjectParamsMap(List<Object> params);
    Map<Integer, String> getObjectParamsMap(String param);
    Map<Integer, Number> getObjectParamsMap(Number param);
}
