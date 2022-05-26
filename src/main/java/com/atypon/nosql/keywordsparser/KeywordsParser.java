package com.atypon.nosql.keywordsparser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeywordsParser {
    private final static String keywordPattern =
            "^([^\\W\\d]+)(\\(((\\w+)(,\\w+)*)\\))?$";

    public Keyword parseKeyword(String keyword) throws InvalidKeywordException {
        Matcher matcher = Pattern.compile(keywordPattern).matcher(keyword);
        if (matcher.find()) {
            return new Keyword(
                    matcher.group(1),
                    Arrays.stream(matcher.group(3).split(", *")).toList()
            );
        } else {
            throw new InvalidKeywordException(keyword + " is not a valid keyword");
        }
    }

    public List<Keyword> parse(String line) throws InvalidKeywordException {
        List<Keyword> keywords = new ArrayList<>();
        for (String keyword : line.split(" +")) {
            keywords.add(parseKeyword(keyword));
        }
        return keywords;
    }
}
