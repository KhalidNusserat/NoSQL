package com.atypon.nosql.keywordsparser;

import java.util.List;

public interface KeywordsParser {
    Keyword parseKeyword(String keyword) throws InvalidKeywordException;

    List<Keyword> parse(String line) throws InvalidKeywordException;
}
