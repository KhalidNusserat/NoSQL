package com.atypon.nosql.database.index;

import com.atypon.nosql.database.document.Document;

public interface IndexFactory {
    Index createFromFields(Document fieldsDocument);
}
