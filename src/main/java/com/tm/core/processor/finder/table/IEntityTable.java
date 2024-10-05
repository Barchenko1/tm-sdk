package com.tm.core.processor.finder.table;

import com.tm.core.processor.finder.parameter.Parameter;

public interface IEntityTable {

//    void addParams(Parameter... params);
    String createFindQuery(Parameter... params);
}
