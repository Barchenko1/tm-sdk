package com.tm.core.manager;

import com.tm.core.dao.common.IEntityDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityOperationManager extends AbstractEntityOperationManager implements IEntityOperationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityOperationManager.class);

    public EntityOperationManager(IEntityDao dao) {
        super(dao);
    }
}
