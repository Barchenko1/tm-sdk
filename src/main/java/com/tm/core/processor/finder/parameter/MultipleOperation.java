package com.tm.core.processor.finder.parameter;

public class MultipleOperation {
    Operation[] operations;
    Operator[] operators;

    public MultipleOperation(Operation[] operations, Operator[] operators) {
        if (operations.length >= operators.length) {
            throw new IllegalArgumentException();
        }
        this.operations = operations;
        this.operators = operators;
    }

    public Operation[] getOperations() {
        return operations;
    }

    public Operator[] getOperators() {
        return operators;
    }
}
