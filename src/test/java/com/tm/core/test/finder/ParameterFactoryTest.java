package com.tm.core.test.finder;

import com.tm.core.processor.finder.parameter.Parameter;
import com.tm.core.processor.finder.factory.ParameterFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParameterFactoryTest {

    private final ParameterFactory parameterFactory = new ParameterFactory();

    @Test
    void createStringParameter_ShouldCreateParameter() {
        String parameterName = "name";
        String parameterValue = "value";

        Parameter parameter = parameterFactory.createStringParameter(parameterName, parameterValue);

        assertNotNull(parameter);
        assertEquals(parameterName, parameter.getName());
        assertEquals(parameterValue, parameter.getValue());
    }

    @Test
    void createIntegerParameter_ShouldCreateParameter() {
        String parameterName = "age";
        Integer parameterValue = 30;

        Parameter parameter = parameterFactory.createIntegerParameter(parameterName, parameterValue);

        assertNotNull(parameter);
        assertEquals(parameterName, parameter.getName());
        assertEquals(parameterValue, parameter.getValue());
    }

    @Test
    void createDoubleParameter_ShouldCreateParameter() {
        String parameterName = "age";
        Double parameterValue = 2.5;

        Parameter parameter = parameterFactory.createDoubleParameter(parameterName, parameterValue);

        assertNotNull(parameter);
        assertEquals(parameterName, parameter.getName());
        assertEquals(parameterValue, parameter.getValue());
    }

    @Test
    void createLongParameter_ShouldCreateParameter() {
        String parameterName = "age";
        Long parameterValue = 1L;

        Parameter parameter = parameterFactory.createLongParameter(parameterName, parameterValue);

        assertNotNull(parameter);
        assertEquals(parameterName, parameter.getName());
        assertEquals(parameterValue, parameter.getValue());
    }

    @Test
    void createBooleanParameter_ShouldCreateParameter() {
        String parameterName = "age";
        Boolean parameterValue = true;

        Parameter parameter = parameterFactory.createBooleanParameter(parameterName, parameterValue);

        assertNotNull(parameter);
        assertEquals(parameterName, parameter.getName());
        assertEquals(parameterValue, parameter.getValue());
    }

    @Test
    void createParameterArray_ShouldCreateParameterArray() {
        Parameter param1 = new Parameter("param1", "value1");
        Parameter param2 = new Parameter("param2", 123);

        Parameter[] parameters = parameterFactory.createParameterArray(param1, param2);

        assertArrayEquals(new Parameter[]{param1, param2}, parameters);
    }

    @Test
    void createParameter_WithNullName_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                parameterFactory.createStringParameter(null, "value"));

        assertEquals("Parameter name cannot be null or empty", exception.getMessage());
    }

    @Test
    void createParameter_WithEmptyName_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                parameterFactory.createStringParameter("", "value"));

        assertEquals("Parameter name cannot be null or empty", exception.getMessage());
    }
}
