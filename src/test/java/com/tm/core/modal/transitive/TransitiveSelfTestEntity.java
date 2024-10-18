package com.tm.core.modal.transitive;

import com.tm.core.modal.TransitiveSelfEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "transitiveSelfTestEntity")
public class TransitiveSelfTestEntity extends TransitiveSelfEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;
    @Column
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transitiveSelfTestEntity_id")
    private TransitiveSelfTestEntity parent;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = CascadeType.ALL)
    private List<TransitiveSelfTestEntity> childNodeList = new ArrayList<>();

    @Override
    public <E extends TransitiveSelfEntity> E getParent() {
        return (E) this.parent;
    }

    @Override
    public void setParent(TransitiveSelfEntity parent) {
        this.parent = (TransitiveSelfTestEntity) parent;
    }

    @Override
    public String getRootField() {
        return this.name;
    }

    @Override
    public <E extends TransitiveSelfEntity> List<E> getChildNodeList() {
        return (List<E>) this.childNodeList;
    }

    @Override
    public <E extends TransitiveSelfEntity> void setChildNodeList(List<E> childNodeList) {
        this.childNodeList = (List<TransitiveSelfTestEntity>) childNodeList;
    }

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

    public void setId(Long id) {
        this.id = id;
    }
}
