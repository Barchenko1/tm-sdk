package com.tm.core.modal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;

@Entity
@Table(name = "TestEntity")
@NamedNativeQueries({
        @NamedNativeQuery(name = "getTestEntityAll",
                query = "select * from testEntity",
                resultClass = TestEntity.class),
        @NamedNativeQuery(name = "getTestEntityById",
                query = "select * from testEntity t where t.id=?",
                resultClass = TestEntity.class),
        @NamedNativeQuery(name = "getTestEntityByName",
                query = "select * from testEntity t where t.name=?",
                resultClass = TestEntity.class),
})
public class TestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column
    private String name;

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
