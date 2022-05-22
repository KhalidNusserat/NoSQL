package com.atypon.document.primitives;

import com.atypon.document.DocumentValue;

import java.util.Objects;

public record StringDocumentValue(String value) implements DocumentValue {
    public static StringDocumentValue fromString(String value) {
        return new StringDocumentValue(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringDocumentValue that = (StringDocumentValue) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "StringDocumentValue{" +
                "value='" + value + '\'' +
                '}';
    }
}
