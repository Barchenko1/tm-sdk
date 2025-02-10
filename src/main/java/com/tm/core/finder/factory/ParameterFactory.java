package com.tm.core.finder.factory;

import com.tm.core.finder.parameter.Parameter;

import java.util.Arrays;

public class ParameterFactory implements IParameterFactory {

    @Override
    public Parameter createStringParameter(String parameterName, String parameterValue) {
        return createParameter(parameterName, parameterValue);
    }

    @Override
    public Parameter createNumberParameter(String parameterName, Number parameterValue) {
        return createParameter(parameterName, parameterValue);
    }

    @Override
    public Parameter createIntegerParameter(String parameterName, Integer parameterValue) {
        return createParameter(parameterName, parameterValue);
    }

    @Override
    public Parameter createDoubleParameter(String parameterName, Double parameterValue) {
        return createParameter(parameterName, parameterValue);
    }

    @Override
    public Parameter createLongParameter(String parameterName, Long parameterValue) {
        return createParameter(parameterName, parameterValue);
    }

    @Override
    public Parameter createBooleanParameter(String parameterName, Boolean parameterValue) {
        return createParameter(parameterName, parameterValue);
    }

    @Override
    public Parameter[] createParameterArray(Parameter... parameters) {
        return Arrays.stream(parameters).toArray(Parameter[]::new);
    }

    private Parameter createParameter(String parameterName, Object parameterValue) {
        if (parameterName == null || parameterName.isEmpty()) {
            throw new IllegalArgumentException("Parameter name cannot be null or empty");
        }
        return new Parameter(parameterName, parameterValue);
    }
}
