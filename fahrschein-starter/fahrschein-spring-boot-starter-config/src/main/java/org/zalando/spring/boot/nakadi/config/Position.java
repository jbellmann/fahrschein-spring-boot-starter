package org.zalando.spring.boot.nakadi.config;

public enum Position {
    BEGIN("begin"), END("end");

    private final String value;

    Position(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}