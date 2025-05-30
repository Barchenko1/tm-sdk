package com.tm.core.finder.parameter;

public class Parameter {
    private String name;
    private Object value;

    public Parameter(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setName(String name) {
        this.name = name;
    }
}
