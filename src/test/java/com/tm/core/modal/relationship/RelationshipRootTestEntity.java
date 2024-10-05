package com.tm.core.modal.relationship;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "relationshipRootTestEntity")
public class RelationshipRootTestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column
    private String name;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private SingleDependentTestEntity singleDependentTestEntity;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DependentTestEntity> dependentTestEntityList;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SingleDependentTestEntity getSingleDependentTestEntity() {
        return singleDependentTestEntity;
    }

    public void setSingleDependentTestEntity(SingleDependentTestEntity singleDependentTestEntity) {
        this.singleDependentTestEntity = singleDependentTestEntity;
    }

    public List<DependentTestEntity> getDependentTestEntityList() {
        return dependentTestEntityList;
    }

    public void setDependentTestEntityList(List<DependentTestEntity> dependentTestEntityList) {
        this.dependentTestEntityList = dependentTestEntityList;
    }
}
