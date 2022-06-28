package com.atypon.nosql.request.handlers;

import com.atypon.nosql.DatabasesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class OperationsHandlers {

    protected DatabasesManager databasesManager;

    @Autowired
    public final void setDatabasesManager(DatabasesManager databasesManager) {
        this.databasesManager = databasesManager;
    }
}
