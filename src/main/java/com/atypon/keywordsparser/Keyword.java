package com.atypon.keywordsparser;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class Keyword {
    private final String name;

    private final List<String> args;

    public Keyword(String name, Collection<String> args) {
        this.name = name;
        this.args = List.copyOf(args);
    }

    public String getName() {
        return name;
    }

    public List<String> getArgs() {
        return args;
    }

    public static Keyword fromString(String name) {
        return new Keyword(name, List.of());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Keyword keyword1 = (Keyword) o;
        return name.equals(keyword1.name) && args.equals(keyword1.args);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, args);
    }
}
