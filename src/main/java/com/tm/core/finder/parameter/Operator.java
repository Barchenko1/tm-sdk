package com.tm.core.finder.parameter;

public enum Operator {
    AND("AND"),
    OR("OR"),
    MORE(">"),
    LESS("<"),
    END(";"),
    EQUAL("="),
    NOT_EQUAL("!=");

    Operator(String value) {
        this.value = value;
    }

    private final String value;

    public String getValue() {
        return this.value;
    }
}
