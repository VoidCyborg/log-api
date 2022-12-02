package ru.voidcyborg.logapi.logger;

import ru.voidcyborg.logapi.appender.Appender;
import ru.voidcyborg.logapi.level.LogLevel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class LoggerGroup {

    private final String name;
    private final Appender appender;
    private final Map<Class<?>, Logger> loggers = new ConcurrentHashMap<>();
    private final Logger defaultLogger;


    LoggerGroup(String name, Appender appender) {
        this.name = name;
        this.appender = appender;
        this.defaultLogger = new Logger(this.appender);
        this.loggers.put(LoggerGroup.class, this.defaultLogger);
    }

    public Logger getLogger() {
        Class<?> frame = LoggerFactory.getClass(2);
        if (frame == null) {
            this.defaultLogger.error("Failed create logger by stack");
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
