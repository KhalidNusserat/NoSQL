package com.atypon.nosql.keywordsparser;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleKeywordsParser implements KeywordsParser {
    private final static String keywordPattern =
            "^([a-zA-Z]+)(\\(([a-zA-Z\\d\\-., ]+)\\))?$";

    @Override
    public Keyword parseKeyword(String keyword) throws InvalidKeywordException {
        Matcher matcher = Pattern.compile(keywordPattern).matcher(keyword);
        if (matcher.find()) {
            List<String> args = new ArrayList<>();
            if (matcher.group(3) != null) {
                Collections.addAll(args, matcher.group(3).split(","));
            }
            return new Keyword(
                    matcher.group(1),
                    args
            );
        } else {
            throw new InvalidKeywordException(keyword + " is not a valid keyword");
        }
    }

    @Override
    public List<Keyword> parse(String line) throws InvalidKeywordException {
        List<Keyword> keywords = new ArrayList<>();
        for (String keyword : line.split(";")) {
            keywords.add(parseKeyword(keyword));
        }
        return keywords;
    }
}
