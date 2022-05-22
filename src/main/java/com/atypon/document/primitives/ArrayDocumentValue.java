package com.atypon.document.primitives;

import com.atypon.document.DocumentValue;

import java.util.ArrayList;
import java.util.List;

public class ArrayDocumentValue<T extends DocumentValue> implements DocumentValue {
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
}
