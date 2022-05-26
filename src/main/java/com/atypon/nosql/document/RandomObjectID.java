package com.atypon.nosql.document;

import java.util.Objects;
import java.util.Random;

public class RandomObjectID implements ObjectID {
    private final long id;

    public RandomObjectID() {
        id = new Random().nextLong();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RandomObjectID that = (RandomObjectID) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return Long.toString(id);
    }
}
