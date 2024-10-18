package com.tm.core.modal;

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
@Table(name = "multi_linked_node")
public class MultiLinkedNode<T> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "value")
    private T value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_id")
    private MultiLinkedNode<T> previous;

    @OneToMany(mappedBy = "previous", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MultiLinkedNode<T>> nextList = new ArrayList<>();

    public MultiLinkedNode() {}

    public MultiLinkedNode(T value) {
        this.value = value;
    }

    public void addNext(MultiLinkedNode<T> nextNode) {
        nextNode.setPrevious(this); // Set the previous reference in the next node
        nextList.add(nextNode);
    }

    public void setPrevious(MultiLinkedNode<T> previousNode) {
        this.previous = previousNode;
    }

    public MultiLinkedNode<T> getPrevious() {
        return this.previous;
    }

    public List<MultiLinkedNode<T>> getNextList() {
        return this.nextList;
    }

    public T getValue() {
        return this.value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void printStructure(String indent) {
        System.out.println(indent + "Node ID: " + id + " | Value: " + value);
        for (MultiLinkedNode<T> nextNode : nextList) {
            nextNode.printStructure(indent + "    ");
        }
    }
}
