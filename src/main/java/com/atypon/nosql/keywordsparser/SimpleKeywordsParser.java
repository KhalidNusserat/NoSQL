package com.atypon.nosql.keywordsparser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleKeywordsParser implements KeywordsParser {
    private static final Pattern keywordPattern = Pattern.compile("^(\\w+) *(\\(([\\w\\d, ]+)\\))?$");

    @Override
    public List<Keyword> parseKeywords(String keywordsString) throws InvalidKeywordException {
        List<Keyword> keywords = new ArrayList<>();
        String[] splitKeywordsStrings = keywordsString.split(" *; *");
        for (String keywordString : splitKeywordsStrings) {
            keywords.add(parseKeyword(keywordString));
        }
        return keywords;
    }

    private Keyword parseKeyword(String keywordString) throws InvalidKeywordException {
        Matcher keywordMatcher = keywordPattern.matcher(keywordString);
        if (keywordMatcher.find()) {
            String keywordName = keywordMatcher.group(1);
            String keywordArgs = keywordMatcher.group(3);
            return new Keyword(
                    keywordName,
                    keywordArgs == null ? new String[0] : keywordArgs.split(",")
            );
        } else {
            throw new InvalidKeywordException("Invalid keyword syntax: " + keywordString);
        }
    }
}
