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
        pathToValues.put(documentPathString, values);
        if (unique) {
            addedValues.add(values);
        }
    }

    @Override
    public void remove(Document document) {
        Document values = document.getValuesToMatch(fields);
        pathToValues.removeByValues(values);
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

    @Override
    public boolean checkUniqueConstraint(Document document) {
        if (unique) {
            return !addedValues.contains(document.getValuesToMatch(fields));
        } else {
            return true;
        }
    }
}
