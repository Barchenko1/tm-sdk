package com.tm.core.modal;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "TestEmployee")
@NamedNativeQueries({
        @NamedNativeQuery(name = "getTestEmployeeAll",
                query = "select * from testEmployee",
                resultClass = TestEmployee.class),
        @NamedNativeQuery(name = "getTestEmployeeByName",
                query = "select * from testEmployee e where e.name=?",
                resultClass = TestEmployee.class),
})
public class TestEmployee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;
    @Column
    private String name;
    @OneToMany(
            mappedBy = "testEmployee",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<TestDependent> testDependents = new ArrayList<>(2);

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

    public List<TestDependent> getTestDependents() {
        return testDependents;
    }

    public void setTestDependents(List<TestDependent> testDependents) {
        this.testDependents = testDependents;
    }

    public void addDependent(TestDependent testDependent) {
        testDependents.add(testDependent);
        testDependent.setTestEmployee(this);
    }
}
