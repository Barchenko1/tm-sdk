package com.tm.core.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

import java.util.List;

@Entity
public class TestClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    private String name;
    private int age;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chieldClass_id")
    private ParentClass parent;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "testClass_childClass",
            joinColumns = { @JoinColumn(name = "testClass_id") },
            inverseJoinColumns = { @JoinColumn(name = "chieldClass_id") }
    )
    private List<ChildClass> childList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public ParentClass getParent() {
        return parent;
    }

    public void setParent(ParentClass parent) {
        this.parent = parent;
    }

    public List<ChildClass> getChildList() {
        return childList;
    }

    public void setChildList(List<ChildClass> childList) {
        this.childList = childList;
    }

    public void setId(long id) {
        this.id = id;
    }
}
