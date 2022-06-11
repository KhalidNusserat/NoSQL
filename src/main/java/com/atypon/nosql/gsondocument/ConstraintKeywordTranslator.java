package com.atypon.nosql.gsondocument;

import com.atypon.nosql.document.InvalidDocumentSchema;
import com.atypon.nosql.gsondocument.constraints.AllMatchConstraint;
import com.atypon.nosql.gsondocument.constraints.Constraint;
import com.atypon.nosql.keywordsparser.InvalidKeywordException;
import com.atypon.nosql.keywordsparser.Keyword;

public interface ConstraintKeywordTranslator {
    Constraint translate(String keywordString) throws InvalidDocumentSchema, InvalidKeywordException;
}
