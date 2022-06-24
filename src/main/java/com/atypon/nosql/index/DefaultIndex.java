package com.atypon.nosql.index;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.utils.ReversedHashMap;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultIndex implements Index {
    private final Document fields;

    private final ReversedHashMap<String, Document> pathToValues;

    private final boolean unique;

    private final Set<Document> addedValues = ConcurrentHashMap.newKeySet();

    public DefaultIndex(Document fields, boolean unique) {
        this.fields = fields;
        this.unique = unique;
        pathToValues = new ReversedHashMap<>();
    }

    @Override
    public void add(Document document, Path documentPath) {
        Document values = document.getValuesToMatch(fields);
        String documentPathString = documentPath.toString();
        if (unique) {
            if (!addedValues.contains(values)) {
                pathToValues.put(documentPathString, values);
                addedValues.add(values);
            } else {
                throw new UniqueIndexViolationException();
            }
        } else {
            pathToValues.put(documentPathString, values);
        }
    }

    @Override
    public void remove(Document document) {
        pathToValues.removeByValue(document.getValuesToMatch(fields));
    }

    @Override
    public Collection<Path> get(Document document) {
        Document values = document.getValuesToMatch(fields);
        return pathToValues.getFromValue(values).stream().map(Path::of).toList();
    }

    @Override
    public boolean contains(Document document) {
        return pathToValues.containsValue(document.getValuesToMatch(fields));
    }

    @Override
    public Document getFields() {
        return fields;
    }
}
