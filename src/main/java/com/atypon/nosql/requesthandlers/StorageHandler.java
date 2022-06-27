package com.atypon.nosql.requesthandlers;

import com.atypon.nosql.request.DatabaseOperation;
import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.response.DatabaseResponse;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Component("defaultHandler")
public class StorageHandler implements DatabaseRequestHandler {

    private final Map<DatabaseOperation, DatabaseRequestHandler> operationHandlerMap = new HashMap<>();

    @Override
    public DatabaseResponse handle(DatabaseRequest request) {
        return operationHandlerMap.get(request.operation()).handle(request);
    }

    public void registerOperations(OperationsHandlers operationsHandlers) {
        Class<? extends OperationsHandlers> operationsHandlersClass = operationsHandlers.getClass();
        Method[] methods = operationsHandlersClass.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(DatabaseOperationMapping.class)) {
                DatabaseOperation operation = method.getAnnotation(DatabaseOperationMapping.class).value();
                DatabaseRequestHandler requestHandler = requestHandlerFromMethod(operationsHandlers, method);
                operationHandlerMap.put(operation, requestHandler);
            }
        }
    }

    private DatabaseRequestHandler requestHandlerFromMethod(OperationsHandlers operationsHandlers, Method method) {
        return request -> {
            try {
                return (DatabaseResponse) method.invoke(operationsHandlers ,request);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
