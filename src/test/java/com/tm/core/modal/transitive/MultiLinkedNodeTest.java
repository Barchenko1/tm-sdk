package com.tm.core.modal.transitive;

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
@Table(name = "multilinkednodetest")
public class MultiLinkedNodeTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "value")
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_id")
    private MultiLinkedNodeTest previous;

    @OneToMany(mappedBy = "previous", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MultiLinkedNodeTest> nextList = new ArrayList<>();

    public MultiLinkedNodeTest() {
    }

    public MultiLinkedNodeTest(String value) {
        this.value = value;
    }

    public void addNext(MultiLinkedNodeTest nextNode) {
        nextNode.setPrevious(this);
        nextList.add(nextNode);
    }

    public void setPrevious(MultiLinkedNodeTest previousNode) {
        this.previous = previousNode;
    }

    public MultiLinkedNodeTest getPrevious() {
        return this.previous;
    }

    public List<MultiLinkedNodeTest> getNextList() {
        return this.nextList;
    }

    public String getValue() {
        return this.value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
