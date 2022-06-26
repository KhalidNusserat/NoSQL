package com.atypon.nosql.gsondocument;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentSchema;
import com.atypon.nosql.document.DocumentSchemaFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GsonDocumentSchemaFactory implements DocumentSchemaFactory {

    private final ConstraintsExtractor constraintsExtractor;

    @Autowired
    public GsonDocumentSchemaFactory(ConstraintsExtractor constraintsExtractor) {
        this.constraintsExtractor = constraintsExtractor;
        DocumentSchema.setSchemaFactory(this);
    }

    @Override
    public GsonDocumentSchema createFromDocument(Document schemaDocument) {
        GsonDocument gsonDocument = (GsonDocument) schemaDocument;
        return new GsonDocumentSchema(gsonDocument, constraintsExtractor);
    }

    @SuppressWarnings("unchecked")
    @Override
    public GsonDocumentSchema createFromClass(Class<?> clazz) {
        Map<String, Object> result = (Map<String, Object>) extractSchemaFromType(clazz);
        Document schemaDocument = Document.createFromMap(result);
        return createFromDocument(schemaDocument);
    }

    private Object extractSchemaFromType(Class<?> fieldType) {
        if (Number.class.isAssignableFrom(fieldType)) {
            return "number";
        } else if (Boolean.class.isAssignableFrom(fieldType)) {
            return "boolean";
        } else if (String.class.isAssignableFrom(fieldType)) {
            return "string";
        } else if (fieldType.isArray()) {
            return List.of(extractSchemaFromType(fieldType.componentType()));
        } else {
            Field[] fields = fieldType.getFields();
            Map<String, Object> result = new HashMap<>();
            for (Field field : fields) {
                result.put(field.getName(), extractSchemaFromType(field.getType()));
            }
            return result;
        }
    }
}
