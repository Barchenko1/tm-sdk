package com.tm.core.dao.factory;

import com.tm.core.modal.GeneralEntity;

import java.util.List;
import java.util.Map;

public class GeneralEntityFactory implements IGeneralEntityFactory{

    @Override
    public GeneralEntity getOneGeneralEntity(Object... values) {
        GeneralEntity generalEntity = new GeneralEntity();
        generalEntity.addEntityPriority(1, values);
        return generalEntity;
    }

    @Override
    public GeneralEntity getOneGeneralEntity(int priority, Object... values) {
        GeneralEntity generalEntity = new GeneralEntity();
        generalEntity.addEntityPriority(priority, values);
        return generalEntity;
    }

    @Override
    public GeneralEntity getMultipleGeneralEntity(Map<Integer, List<Object>> priorityValuesMap) {
        GeneralEntity generalEntity = new GeneralEntity();
        priorityValuesMap.forEach(generalEntity::addEntityPriority);
        return generalEntity;
    }


}
