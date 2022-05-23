package com.atypon.document;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.Iterator;
import java.util.List;

public class ArrayDocumentValue<T extends DocumentValue> implements DocumentValue, Iterable<T> {
    private final List<T> list;

    public ArrayDocumentValue(List<T> list) {
        this.list = ImmutableList.copyOf(list);
    }

    public static <T extends DocumentValue> ArrayDocumentValue<T> copyOf(List<T> list) {
        return new ArrayDocumentValue<>(list);
    }

    public T get(int index) {
        return list.get(index);
    }

    public ArrayDocumentValue<T> with(T value) {
        return ArrayDocumentValue.copyOf(
                ImmutableList.<T>builder()
                        .addAll(list)
                        .add(value)
                        .build()
        );
    }

    public ArrayDocumentValue<T> withAll(List<T> list) {
        return ArrayDocumentValue.copyOf(
                ImmutableList.<T>builder()
                        .addAll(this.list)
                        .addAll(list)
                        .build()
        );
    }

    public ArrayDocumentValue<T> without(T value) {
        Preconditions.checkNotNull(value);
        return ArrayDocumentValue.copyOf(
                list.stream().filter(t -> !t.equals(value)).toList()
        );
    }

    public ArrayDocumentValue<T> withoutAll(List<T> list) {
        Preconditions.checkNotNull(list);
        return ArrayDocumentValue.copyOf(
                this.list.stream().filter(t -> !list.contains(t)).toList()
        );
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }
}
