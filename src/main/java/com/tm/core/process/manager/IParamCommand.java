package com.tm.core.process.manager;

import com.tm.core.finder.parameter.Parameter;

public interface IParamCommand extends ICommand{
    <E> void deleteEntityByParameter(Class<E> clazz, Parameter parameter);
}
