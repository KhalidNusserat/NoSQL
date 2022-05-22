package com.atypon.document.primitives;

import com.atypon.document.DocumentValue;

import java.util.Objects;

public record BooleanDocumentValue(Boolean value) implements DocumentValue, PrimitiveDocumentValue<Boolean> {
    public static BooleanDocumentValue fromBoolean(boolean value) {
        return new BooleanDocumentValue(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BooleanDocumentValue that = (BooleanDocumentValue) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "BooleanDocumentValue{" +
                "value=" + value +
                '}';
    }
}
