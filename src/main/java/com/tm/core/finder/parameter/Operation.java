package com.tm.core.finder.parameter;

public class Operation {
    private String name;
    private Operator operator;
    private Object value;

    public Operation(String name, Operator operator, Object value) {
        this.name = name;
        this.operator = operator;
        this.value = value;
    }

    public Operation(String name, Object value) {
        this.name = name;
        this.operator = Operator.EQUAL;
        this.value = value;
    }

    public String create2params(Parameter parameter1, Parameter parameter2, Operator operator) {
        return String.format("%s %s %s %s %s %s %s%s",
                parameter1.getName(),
                Operator.EQUAL.getValue(),
                parameter1.getValue(),
                operator.getValue(),
                parameter2.getName(),
                Operator.EQUAL.getValue(),
                parameter2.getValue(),
                Operator.END.getValue());
    }

    public String createOperationParams(Parameter[] parameters, Operator operator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parameters.length; i++) {
            sb.append(parameters[i].getName());
            sb.append(Operator.EQUAL.getValue());
            sb.append(parameters[i].getValue());
            if (i < parameters.length - 1) {
                sb.append(operator.getValue());
            }
        }
        sb.append(Operator.END.getValue());
        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public Operator getOperator() {
        return operator;
    }

    public Object getValue() {
        return value;
    }

    private enum Operator {
        MORE(">"),
        LESS("<"),
        END(";"),
        EQUAL("="),
        NOT_EQUAL("!=");

        private final String value;

        Operator(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
