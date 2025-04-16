package com.tm.core.finder.factory;

import com.tm.core.finder.parameter.Parameter;

import java.util.List;

public interface IParameterFactory {
    Parameter createStringParameter(String parameterName, String parameterValue);
    Parameter createNumberParameter(String parameterName, Number parameterValue);
    Parameter createIntegerParameter(String parameterName, Integer parameterValue);
    Parameter createDoubleParameter(String parameterName, Double parameterValue);
    Parameter createLongParameter(String parameterName, Long parameterValue);
    Parameter createBooleanParameter(String parameterName, Boolean parameterValue);
    Parameter createEnumParameter(String parameterName, Enum<?> parameterValue);
    //
    Parameter createStringParameterList(String parameterName, List<String> parameterValues);
    Parameter[] createParameterArray(Parameter... parameters);
}
