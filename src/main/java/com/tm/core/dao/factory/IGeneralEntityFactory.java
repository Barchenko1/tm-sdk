package com.tm.core.dao.factory;

import com.tm.core.modal.GeneralEntity;

import java.util.List;
import java.util.Map;

public interface IGeneralEntityFactory {
    GeneralEntity getOneGeneralEntity(Object... values);
    GeneralEntity getOneGeneralEntity(int priority, Object... values);
    GeneralEntity getMultipleGeneralEntity(Map<Integer, List<Object>> priorityValuesMap);
}
