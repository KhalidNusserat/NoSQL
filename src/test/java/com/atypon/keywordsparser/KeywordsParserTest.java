package com.atypon.keywordsparser;

import com.atypon.nosql.keywordsparser.InvalidKeywordException;
import com.atypon.nosql.keywordsparser.Keyword;
import com.atypon.nosql.keywordsparser.KeywordsParser;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

class KeywordsParserTest {

    @Test
    void parseKeyword() throws InvalidKeywordException {
        String line = "test(arg,arg1,10,10)";
        Keyword keyword = new Keyword(
                "test",
                List.of("arg", "arg1", "10", "10")
        );
        assertEquals(keyword, new KeywordsParser().parseKeyword(line));
    }

    @Test
    void parse() throws InvalidKeywordException {
        String line = "test(arg,arg1,10,10)   anotherTest(A,B,123)";
        Keyword keyword1 = new Keyword(
                "test",
                List.of("arg", "arg1", "10", "10")
        );
        Keyword keyword2 = new Keyword(
                "anotherTest",
                List.of("A", "B", "123")
        );
        assertEquals(List.of(keyword1, keyword2), new KeywordsParser().parse(line));
    }
}