package com.tm.core.processor.finder.parameter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParameterManager {
    private Map<Class<?>, Parameter> parameterMap = new HashMap<>();
    private Map<Class<?>, Parameter[]> parameterArrayMap = new HashMap<>();
    private Operator operator;

    public Parameter getParameter(Class<?> clazz) {
        return parameterMap.get(clazz);
    }

    public Parameter[] getParameterArray(Class<?> clazz) {
        return parameterArrayMap.get(clazz);
    }
}
