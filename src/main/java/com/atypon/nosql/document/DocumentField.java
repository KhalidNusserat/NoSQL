package com.atypon.nosql.document;

import com.google.common.collect.ImmutableList;
import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class DocumentField implements Iterable<String> {
    private final List<String> fieldComponents;

    public DocumentField(List<String> fieldComponents) {
        this.fieldComponents = ImmutableList.copyOf(fieldComponents);
    }

    public DocumentField(String... fieldComponents) {
        this.fieldComponents = ImmutableList.copyOf(fieldComponents);
    }

    public static DocumentField of(List<String> fieldComponents) {
        return new DocumentField(fieldComponents);
    }

    public static DocumentField of(String... fieldComponents) {
        return new DocumentField(fieldComponents);
    }

    public DocumentField with(String part) {
        List<String> newComponents = new ArrayList<>(fieldComponents);
        newComponents.add(part);
        return new DocumentField(newComponents);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentField that = (DocumentField) o;
        return fieldComponents.equals(that.fieldComponents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldComponents);
    }

    @Override
    public String toString() {
        return "DocumentField{" + fieldComponents + '}';
    }

    @NotNull
    @Override
    public Iterator<String> iterator() {
        return fieldComponents.iterator();
    }
}
