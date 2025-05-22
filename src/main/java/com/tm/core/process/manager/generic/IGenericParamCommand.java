package com.tm.core.process.manager.generic;

import com.tm.core.finder.parameter.Parameter;

public interface IGenericParamCommand {
    <E> void deleteEntityByParameter(Class<E> clazz, Parameter parameter);
}
