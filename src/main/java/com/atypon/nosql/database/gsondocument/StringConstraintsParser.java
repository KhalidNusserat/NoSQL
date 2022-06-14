package com.atypon.nosql.database.gsondocument;

import com.atypon.nosql.database.document.InvalidDocumentSchema;
import com.atypon.nosql.database.gsondocument.constraints.Constraint;
import com.atypon.nosql.database.gsondocument.constraints.Constraints;
import com.atypon.nosql.database.gsondocument.constraints.TypeConstraint;
import com.atypon.nosql.database.keywordsparser.InvalidKeywordException;
import com.atypon.nosql.database.keywordsparser.Keyword;
import com.atypon.nosql.database.keywordsparser.KeywordsParser;
import com.atypon.nosql.database.keywordsparser.SimpleKeywordsParser;

import java.util.List;

public class StringConstraintsParser {
    private final KeywordsParser keywordsParser = new SimpleKeywordsParser();

    public Constraint extractConstraints(String constraint) throws InvalidKeywordException, InvalidDocumentSchema {
        Constraints constraints = Constraints.empty();
        List<Keyword> keywords = keywordsParser.parseKeywords(constraint);
        for (Keyword keyword : keywords) {
            constraints.add(extractConstraints(keyword));
        }
        return constraints;
    }

    private Constraint extractConstraints(Keyword keyword) throws InvalidDocumentSchema {
        return switch (keyword.getName()) {
            case "number" -> TypeConstraint.match(Number.class);
            case "string" -> TypeConstraint.match(String.class);
            case "boolean" -> TypeConstraint.match(Boolean.class);
            default -> throw new InvalidDocumentSchema("Unrecognized type provided: " + keyword.getName());
        };
    }
}
