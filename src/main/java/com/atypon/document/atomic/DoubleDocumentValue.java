package com.atypon.document.atomic;

import com.atypon.document.DocumentValue;

import java.util.Objects;

public record DoubleDocumentValue(Double value) implements DocumentValue, AtomicDocumentValue<Double> {
    public static DoubleDocumentValue fromDouble(double value) {
        return new DoubleDocumentValue(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DoubleDocumentValue that = (DoubleDocumentValue) o;
        return Double.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "DoubleDocumentValue{" +
                "value=" + value +
                '}';
    }
}
