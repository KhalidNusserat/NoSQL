package com.atypon.nosql.keywordsparser;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

class KeywordsParserTest {

    @Test
    void parseKeyword() throws InvalidKeywordException {
        String line1 = "test(arg,arg1,10,10)";
        Keyword keyword1 = new Keyword(
                "test",
                List.of("arg", "arg1", "10", "10")
        );
        assertEquals(keyword1, new KeywordsParser().parseKeyword(line1));

        assertEquals(Keyword.fromString("string"), new KeywordsParser().parseKeyword("string"));
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
        assertEquals(List.of(keyword1, keyword2, Keyword.fromString("required")), new KeywordsParser().parse(line));
    }
}