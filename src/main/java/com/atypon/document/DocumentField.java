package com.atypon.document;

import java.util.Objects;

public class DocumentField {
    private static final String regex = "^[^_\\d\\W][\\w_\\d]*$";

    private final String field;

    private DocumentField(String field) throws IllegalFieldNameException {
        if (!field.matches(regex)) {
            throw new IllegalFieldNameException(field);
        }
        this.field = field;
    }

    public static DocumentField fromString(String field) throws IllegalFieldNameException {
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
