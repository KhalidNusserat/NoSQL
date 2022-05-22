package com.atypon.document.primitives;

import com.atypon.document.DocumentValue;

import java.util.Objects;

public record IntegerDocumentValue(int value) implements DocumentValue {
    public static IntegerDocumentValue fromInt(int value) {
        return new IntegerDocumentValue(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntegerDocumentValue that = (IntegerDocumentValue) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "IntegerDocumentValue{" +
                "value=" + value +
                '}';
    }
}
