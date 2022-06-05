package com.atypon.nosql.keywordsparser;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleKeywordsParserTest {

    @Test
    void parseKeyword() throws InvalidKeywordException {
        String line1 = "test(arg,arg1,10,10)";
        Keyword keyword1 = new Keyword(
                "test",
                List.of("arg", "arg1", "10", "10")
        );
        assertEquals(keyword1, new SimpleKeywordsParser().parseKeyword(line1));

        assertEquals(Keyword.fromString("string"), new SimpleKeywordsParser().parseKeyword("string"));
    }

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
        assertEquals(List.of(keyword1, keyword2, Keyword.fromString("required")), new SimpleKeywordsParser().parse(line));
    }
}