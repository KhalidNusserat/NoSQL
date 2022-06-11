package com.atypon.nosql.gsondocument.constraints;

import com.atypon.nosql.document.InvalidDocumentSchema;
import com.google.gson.JsonElement;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegexConstraint implements Constraint {
    private final Pattern pattern;

    public RegexConstraint(String regex) throws InvalidDocumentSchema {
        try {
            this.pattern = Pattern.compile(regex);
        } catch (PatternSyntaxException e) {
            throw new InvalidDocumentSchema("Invalid regex provided: " + e);
        }
    }

    public static RegexConstraint match(String regex) throws InvalidDocumentSchema {
        return new RegexConstraint(regex);
    }

    @Override
    public boolean validate(JsonElement jsonElement) {
        return pattern.matcher(jsonElement.getAsString()).matches();
    }
}
