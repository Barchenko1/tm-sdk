package com.tm.core.modal.transitive;

import com.tm.core.modal.TransitiveSelfEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "transitiveSelfTestEntity")
public class TransitiveSelfTestEntity extends TransitiveSelfEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column
    private String name;

    @ManyToOne
    @JoinColumn(name = "transitiveSelfTestEntity_id")
    private TransitiveSelfTestEntity parent;
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<TransitiveSelfTestEntity> childNodeList;

    @Override
    public <E extends TransitiveSelfEntity> E getParent() {
        return (E) parent;
    }

    @Override
    public String getRootField() {
        return name;
    }

    @Override
    public <E extends TransitiveSelfEntity> List<E> getChildNodeList() {
        return (List<E>) childNodeList;
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

}
