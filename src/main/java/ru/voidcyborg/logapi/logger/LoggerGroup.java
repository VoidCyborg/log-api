package ru.voidcyborg.logapi.logger;

import ru.voidcyborg.logapi.appender.Appender;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class LoggerGroup {

    private final String name;
    private final Set<Appender> appenders = ConcurrentHashMap.newKeySet();
    private final Map<Class<?>, Logger> loggers = new ConcurrentHashMap<>();
    private final Logger defaultLogger = loggers.computeIfAbsent(LoggerGroup.class, clazz -> new Logger(appenders));


    LoggerGroup(String name) {
        this.name = name;
    }

    public Logger getLogger() {
        Class<?> frame = LoggerFactory.getClass(2);
        if (frame == null) {
            this.defaultLogger.error("Failed to create logger by stack");
            return this.defaultLogger;
        }

        return loggers.computeIfAbsent(frame, clazz -> {
            defaultLogger.info("Created new logger - " + clazz.getSimpleName());
            return new Logger(this.appender);
        });
    }

    public String getName() {
        return name;
    }
}
