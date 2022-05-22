package com.atypon.document.primitives;

import com.atypon.document.DocumentValue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ArrayDocumentValue<T extends DocumentValue> implements DocumentValue, Iterable<T> {
    private final List<T> list;

    public ArrayDocumentValue() {
        list = new ArrayList<>();
    }

    public ArrayDocumentValue(List<T> list) {
        this.list = List.copyOf(list);
    }

    public static <T extends DocumentValue> ArrayDocumentValue<T> fromList(List<T> list) {
        return new ArrayDocumentValue<>(list);
    }

    public T get(int index) {
        return list.get(index);
    }

    public ArrayDocumentValue<T> addAll(List<T> list) {
        List<T> newList = new ArrayList<>(list);
        newList.addAll(this.list);
        return new ArrayDocumentValue<>(newList);
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }
}
