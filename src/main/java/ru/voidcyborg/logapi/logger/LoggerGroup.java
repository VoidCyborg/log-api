package ru.voidcyborg.logapi.logger;

import ru.voidcyborg.logapi.appender.Appender;
import ru.voidcyborg.logapi.level.LogLevel;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class LoggerGroup {

    private final LogLevel level;
    private final Set<Appender> appenders = ConcurrentHashMap.newKeySet();
    private final Map<Class<?>, Logger> loggers = new ConcurrentHashMap<>();
    private final Logger defaultLogger;


    LoggerGroup(LogLevel level) {
        this.level = level;
        this.defaultLogger = loggers.computeIfAbsent(LoggerGroup.class, clazz -> new Logger(this.appenders, this.level));
    }

    public Logger getLogger() {
        Class<?> frame = LoggerFactory.getClass(2);

        if (frame == null) {
            this.defaultLogger.error("Failed to create logger by stack");
            return this.defaultLogger;
        }

        return loggers.computeIfAbsent(frame, clazz -> {
            defaultLogger.trace("Created new logger - " + clazz.getSimpleName() + " - " + this.level);
            return new Logger(this.appenders, this.level);
        });
    }

    public LoggerGroup addAppender(Appender appender) {
        if (appender != null) {
            appenders.add(appender);
        }
        return this;
    }

    public LoggerGroup addAppenders(Appender... appenders) {
        if (appenders == null) return this;
        try {
            for (Appender appender : appenders) {
                if (appender != null) this.appenders.add(appender);
            }
        } catch (Exception e) {
            this.defaultLogger.error("Failed to add Appenders to LoggerGroup.");
        }

        return this;
    }

}
