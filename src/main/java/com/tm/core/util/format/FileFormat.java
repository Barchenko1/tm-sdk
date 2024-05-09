package com.tm.core.util.format;

public enum FileFormat {
    XML("xml"), PROPERTIES("properties");

    private final String value;

    FileFormat(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
