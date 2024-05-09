package com.tm.core.dto;

import java.util.ArrayList;
import java.util.List;

public class TestDto {
    private String name;
    private int dtoAge;
    private ParentClass parent;
    private List<ChildClass> childClassList = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDtoAge() {
        return dtoAge;
    }

    public void setDtoAge(int dtoAge) {
        this.dtoAge = dtoAge;
    }

    public ParentClass getParent() {
        return parent;
    }

    public void setParent(ParentClass parent) {
        this.parent = parent;
    }

    public List<ChildClass> getChildClassList() {
        return childClassList;
    }

    public void setChildClassList(List<ChildClass> childClassList) {
        this.childClassList = childClassList;
    }
}
