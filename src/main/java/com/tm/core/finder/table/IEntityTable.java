package com.tm.core.finder.table;

import com.tm.core.finder.parameter.Parameter;

public interface IEntityTable {

    String createFindQuery(Parameter... params);
    String createFindJqlQuery(Parameter... params);
}
