package com.atypon.nosql.request.handlers;

import com.atypon.nosql.request.DatabaseOperation;
import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.request.annotations.DatabaseOperationMapping;
import com.atypon.nosql.request.filters.DatabaseRequestFilter;
import com.atypon.nosql.response.DatabaseResponse;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("defaultHandler")
public class DefaultDatabaseOperationsHandler implements DatabaseRequestHandler {

    private final Map<DatabaseOperation, DatabaseRequestHandler> operationToHandler = new HashMap<>();

    private final List<DatabaseRequestFilter> filters;

    public DefaultDatabaseOperationsHandler(
            List<OperationsHandlers> operationsHandlersList,
            List<DatabaseRequestFilter> filters) {
        this.filters = filters;
        operationsHandlersList.forEach(this::registerOperationsHandlers);
    }

    @Override
    public DatabaseResponse handle(DatabaseRequest request) {
        return operationToHandler.get(request.operation()).handle(request);
    }

    private void registerOperationsHandlers(OperationsHandlers operationsHandlers) {
        Class<? extends OperationsHandlers> operationsHandlersClass = operationsHandlers.getClass();
        Method[] methods = operationsHandlersClass.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(DatabaseOperationMapping.class)) {
                DatabaseOperation operation = method.getAnnotation(DatabaseOperationMapping.class).value();
                DatabaseRequestHandler requestHandler = requestHandlerFromMethod(operationsHandlers, method);
                operationToHandler.put(operation, requestHandler);
            }
        }
    }

    private DatabaseRequestHandler requestHandlerFromMethod(OperationsHandlers operationsHandlers, Method method) {
        return request -> {
            try {
                for (DatabaseRequestFilter filter : filters) {
                    request = filter.filter(request);
                }
                return (DatabaseResponse) method.invoke(operationsHandlers, request);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
