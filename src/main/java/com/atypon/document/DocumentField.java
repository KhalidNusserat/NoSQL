package com.atypon.document;

import java.util.Objects;

public class DocumentField {
    private static final String regex = "^[^_\\d\\W][\\w_\\d]*$";

    private final String field;

    private DocumentField(String field) throws InvalidFieldNameException {
        if (!field.matches(regex)) {
            throw new InvalidFieldNameException(field);
        }
        this.field = field;
    }

    public static DocumentField fromString(String field) throws InvalidFieldNameException {
        return new DocumentField(field);
    }

    public String getField() {
        return field;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentField that = (DocumentField) o;
        return field.equals(that.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field);
    }

    @Override
    public String toString() {
        return "DocumentField{" +
                "field='" + field + '\'' +
                '}';
    }
}
