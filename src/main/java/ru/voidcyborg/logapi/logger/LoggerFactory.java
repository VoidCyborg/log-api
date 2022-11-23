package ru.voidcyborg.logapi.logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class LoggerFactory {

    private static final Map<Class<?>, LoggerGroup> loggerGroups = new ConcurrentHashMap<>();
    private static final LoggerGroup defaultGroup = loggerGroups.put(null, new LoggerGroup(null));
    static {

    }

    public static LoggerGroup getLoggerGroup(Class<?> clazz) {
        return loggerGroups.computeIfAbsent(clazz, LoggerGroup::new);
    }

    private static void initSettings()
}
