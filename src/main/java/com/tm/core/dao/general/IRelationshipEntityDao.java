package com.tm.core.dao.general;

import com.tm.core.modal.RelationshipEntity;

public interface IRelationshipEntityDao {
    void saveRelationshipEntity(RelationshipEntity relationshipEntity);
    void updateRelationshipEntity(RelationshipEntity relationshipEntity);
    void deleteRelationshipEntity(RelationshipEntity relationshipEntity);

}
