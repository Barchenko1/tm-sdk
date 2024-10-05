package com.tm.core.processor.finder.factory;

import com.tm.core.processor.finder.parameter.Parameter;

public interface IParameterFactory {
    Parameter createStringParameter(String parameterName, String parameterValue);
    Parameter createIntegerParameter(String parameterName, Integer parameterValue);
    Parameter createDoubleParameter(String parameterName, Double parameterValue);
    Parameter createLongParameter(String parameterName, Long parameterValue);
    Parameter createBooleanParameter(String parameterName, Boolean parameterValue);
    Parameter[] createParameterArray(Parameter... parameters);
}
