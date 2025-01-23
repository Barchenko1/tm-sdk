package com.tm.core.modal.relationship;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "employee")
@NamedEntityGraph(
        name = "Employee.full",
        attributeNodes = {
                @NamedAttributeNode("spouse"),
                @NamedAttributeNode("dependentList"),
                @NamedAttributeNode("itemSet")
        }
)
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column
    private String name;
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "dependent_id", referencedColumnName = "id")
    private Dependent spouse;
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Dependent> dependentList = new ArrayList<>();
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "employee_item",
            joinColumns = {@JoinColumn(name = "employee_id")},
            inverseJoinColumns = {@JoinColumn(name = "itemset_id")}
    )
    private Set<Item> itemSet = new HashSet<>();

    public void addItem(Item item) {
        this.itemSet.add(item);
        item.getEmployeeSet().add(this);
    }

    public void removeItem(Item item) {
        this.itemSet.remove(item);
        item.getEmployeeSet().remove(this);
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

    public Dependent getSpouse() {
        return spouse;
    }

    public void setSpouse(Dependent spouse) {
        this.spouse = spouse;
    }

    public List<Dependent> getDependentList() {
        return dependentList;
    }

    public void setDependentList(List<Dependent> dependentList) {
        this.dependentList = dependentList;
    }

    public Set<Item> getItemSet() {
        return itemSet;
    }

    public void setItemSet(Set<Item> itemSet) {
        this.itemSet = itemSet;
    }
}
