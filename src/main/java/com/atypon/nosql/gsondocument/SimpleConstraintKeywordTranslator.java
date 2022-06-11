package com.atypon.nosql.gsondocument;

import com.atypon.nosql.document.InvalidDocumentSchema;
import com.atypon.nosql.gsondocument.constraints.*;
import com.atypon.nosql.keywordsparser.InvalidKeywordException;
import com.atypon.nosql.keywordsparser.Keyword;
import com.atypon.nosql.keywordsparser.KeywordsParser;
import com.atypon.nosql.keywordsparser.SimpleKeywordsParser;
import org.jetbrains.annotations.NotNull;

public class SimpleConstraintKeywordTranslator implements ConstraintKeywordTranslator {
    private static final KeywordsParser keywordParser = new SimpleKeywordsParser();

    @Override
    public Constraint translate(String keywordString) throws InvalidDocumentSchema, InvalidKeywordException {
        Keyword keyword = keywordParser.parseKeyword(keywordString);
        return switch (keyword.getName()) {
            case "number" -> getNumberTypeConstraint(keyword);
            case "string" -> getStringTypeConstraint(keyword);
            case "boolean" -> getBooleanTypeConstraint(keyword);
            case "range" -> getRangeConstraint(keyword);
            case "regex" -> getRegexConstraint(keyword);
            case "all" -> getAllMatchConstraint(keyword);
            case "any" -> getAnyMatchConstraint(keyword);
            default -> throw new InvalidDocumentSchema("Invalid constraint: " + keyword.getName());
        };
    }

    @NotNull
    private TypeConstraint getNumberTypeConstraint(Keyword keyword) throws InvalidDocumentSchema {
        if (keyword.getArgsCount() != 0) {
            throw new InvalidDocumentSchema("number doesn't expect an argument");
        }
        return TypeConstraint.match(Number.class);
    }

    @NotNull
    private TypeConstraint getStringTypeConstraint(Keyword keyword) throws InvalidDocumentSchema {
        if (keyword.getArgsCount() != 0) {
            throw new InvalidDocumentSchema("string doesn't expect an argument");
        }
        return TypeConstraint.match(String.class);
    }

    @NotNull
    private TypeConstraint getBooleanTypeConstraint(Keyword keyword) throws InvalidDocumentSchema {
        if (keyword.getArgsCount() != 0) {
            throw new InvalidDocumentSchema("boolean doesn't expect an argument");
        }
        return TypeConstraint.match(Boolean.class);
    }

    @NotNull
    private AnyMatchConstraint getAnyMatchConstraint(Keyword keyword)
            throws InvalidDocumentSchema, InvalidKeywordException {
        if (keyword.getArgsCount() == 0) {
            throw new InvalidDocumentSchema("any expects at least one argument");
        }
        AnyMatchConstraint constraints = AnyMatchConstraint.empty();
        for (String constraint : keyword) {
            constraints.add(translate(constraint));
        }
        return constraints;
    }

    @NotNull
    private AllMatchConstraint getAllMatchConstraint(Keyword keyword)
            throws InvalidDocumentSchema, InvalidKeywordException {
        if (keyword.getArgsCount() == 0) {
            throw new InvalidDocumentSchema("all expects at least one argument");
        }
        AllMatchConstraint constraints = AllMatchConstraint.empty();
        for (String constraint : keyword) {
            constraints.add(translate(constraint));
        }
        return constraints;
    }

    @NotNull
    private RegexConstraint getRegexConstraint(Keyword keyword) throws InvalidDocumentSchema {
        if (keyword.getArgsCount() != 1) {
            throw new InvalidDocumentSchema("regex expects one argument, instead got: " + keyword.getArgsCount());
        }
        return RegexConstraint.match(keyword.getArg(0));
    }

    @NotNull
    private RangeConstraint getRangeConstraint(Keyword keyword) throws InvalidDocumentSchema {
        if (keyword.getArgsCount() != 2) {
            throw new InvalidDocumentSchema("range expects two arguments, instead got: " + keyword.getArgsCount());
        }
        try {
            double firstNumber = Double.parseDouble(keyword.getArg(0));
            double secondNumber = Double.parseDouble(keyword.getArg(1));
            return RangeConstraint.between(firstNumber, secondNumber);
        } catch (NumberFormatException e) {
            throw new InvalidDocumentSchema(
                    "Expected a numbers as arguments for range, instead got: "
                            + keyword.getArg(0) + " and " + keyword.getArg(1)
            );
        }
    }
}
