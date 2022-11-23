package ru.voidcyborg.logapi.logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class LoggerGroup {

    private final Class<?> parentClass;
    private final Map<String, Logger> loggers = new ConcurrentHashMap<>();
    private final Logger defaultLogger = loggers.computeIfAbsent(LoggerGroup.class.getSimpleName(), Logger::new);


    LoggerGroup(Class<?> parent) {
        this.parentClass = parent;
    }

    public Class<?> getParentClass() {
        return parentClass;
    }

    public Logger getLogger(String string) {
        if(string == null) return defaultLogger;
        if(string.equals(LoggerGroup.class.getSimpleName()) || string.equalsIgnoreCase(Lo))
        return loggers.computeIfAbsent(string, name -> {
            defaultLogger.info("Created new Logger(" + string + ")");

        });
    }
}
