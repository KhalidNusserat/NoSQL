package com.atypon.nosql.database.keywordsparser;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Keyword implements Iterable<String> {
    private final String name;

    private final List<String> args;

    public Keyword(String name, Collection<String> args) {
        this.name = name;
        this.args = List.copyOf(args);
    }

    public Keyword(String name, String[] args) {
        this.name = name;
        this.args = List.of(args);
    }

    public Keyword(String name) {
        this.name = name;
        args = List.of();
    }

    public static Keyword fromString(String name) {
        return new Keyword(name, List.of());
    }

    public String getName() {
        return name;
    }

    public int getArgsCount() {
        return args.size();
    }

    public String getArg(int index) {
        return args.get(index);
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

    @Override
    public String toString() {
        return "Keyword{" +
                "name='" + name + '\'' +
                ", args=" + args +
                '}';
    }

    @NotNull
    @Override
    public Iterator<String> iterator() {
        return args.iterator();
    }
}
