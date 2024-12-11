package com.tm.core.processor.finder.table;

import com.tm.core.processor.finder.parameter.Parameter;

public interface IEntityTable {

    String createFindQuery(Parameter... params);
}
