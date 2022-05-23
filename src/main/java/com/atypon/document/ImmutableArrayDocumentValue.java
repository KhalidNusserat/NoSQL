package com.atypon.document;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.Iterator;
import java.util.List;

public class ImmutableArrayDocumentValue<T extends DocumentValue> implements DocumentValue, Iterable<T> {
    private final List<T> list;

    public ImmutableArrayDocumentValue(List<T> list) {
        this.list = ImmutableList.copyOf(list);
    }

    public static <T extends DocumentValue> ImmutableArrayDocumentValue<T> copyOf(List<T> list) {
        return new ImmutableArrayDocumentValue<>(list);
    }

    public T get(int index) {
        return list.get(index);
    }

    public synchronized ImmutableArrayDocumentValue<T> with(T value) {
        return ImmutableArrayDocumentValue.copyOf(
                ImmutableList.<T>builder()
                        .addAll(list)
                        .add(value)
                        .build()
        );
    }

    public synchronized ImmutableArrayDocumentValue<T> withAll(List<T> list) {
        return ImmutableArrayDocumentValue.copyOf(
                ImmutableList.<T>builder()
                        .addAll(this.list)
                        .addAll(list)
                        .build()
        );
    }

    public ImmutableArrayDocumentValue<T> without(T value) {
        Preconditions.checkNotNull(value);
        return ImmutableArrayDocumentValue.copyOf(
                list.stream().filter(t -> !t.equals(value)).toList()
        );
    }

    public ImmutableArrayDocumentValue<T> withoutAll(List<T> list) {
        Preconditions.checkNotNull(list);
        return ImmutableArrayDocumentValue.copyOf(
                this.list.stream().filter(t -> !list.contains(t)).toList()
        );
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }
}
