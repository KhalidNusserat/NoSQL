package com.atypon.nosql.requesthandlers;

import com.atypon.nosql.DatabasesManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class OperationsHandlers implements InitializingBean {

    private DefaultDatabaseOperationsHandler defaultDatabaseOperationsHandler;

    protected DatabasesManager databasesManager;

    @Autowired
    public final void setStorageHandler(DefaultDatabaseOperationsHandler defaultDatabaseOperationsHandler) {
        this.defaultDatabaseOperationsHandler = defaultDatabaseOperationsHandler;
    }

    @Autowired
    public final void setDatabasesManager(DatabasesManager databasesManager) {
        this.databasesManager = databasesManager;
    }

    @Override
    public void afterPropertiesSet() {
        defaultDatabaseOperationsHandler.registerOperations(this);
    }
}
