package com.atypon.nosql.keywordsparser;

import java.util.List;

public interface KeywordsParser {
    List<Keyword> parseKeywords(String keywords) throws InvalidKeywordException;

    Keyword parseKeyword(String keywordString) throws InvalidKeywordException;
}
