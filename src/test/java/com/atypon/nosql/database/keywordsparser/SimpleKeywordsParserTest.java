package com.atypon.nosql.database.keywordsparser;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleKeywordsParserTest {
    private final KeywordsParser keywordsParser = new SimpleKeywordsParser();

    @Test
    void parse() throws InvalidKeywordException {
        String line = "test(arg,arg1,10,10);anotherTest(A,B,123,more than one word,two  spaces);required";
        Keyword keyword1 = new Keyword(
                "test",
                List.of("arg", "arg1", "10", "10")
        );
        Keyword keyword2 = new Keyword(
                "anotherTest",
                List.of("A", "B", "123", "more than one word", "two  spaces")
        );
        assertEquals
                (List.of(keyword1, keyword2, Keyword.fromString("required")),
                        keywordsParser.parseKeywords(line)
                );
    }
}