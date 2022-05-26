package com.atypon.keywordsparser;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class Keyword {
    private final String keyword;

    private final List<String> args;

    public Keyword(String keyword, Collection<String> args) {
        this.keyword = keyword;
        this.args = List.copyOf(args);
    }

    public String getKeyword() {
        return keyword;
    }

    public List<String> getArgs() {
        return args;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Keyword keyword1 = (Keyword) o;
        return keyword.equals(keyword1.keyword) && args.equals(keyword1.args);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyword, args);
    }
}
