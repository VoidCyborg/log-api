package ru.voidcyborg.logapi.logger;

import ru.voidcyborg.logapi.appender.Appender;
import ru.voidcyborg.logapi.appender.appenders.ConsoleAppender;
import ru.voidcyborg.logapi.level.LogLevel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class LoggerFactory {

    private static final Map<Class<?>, LoggerGroup> loggerGroups = new ConcurrentHashMap<>();
    private static final Appender defaultAppender = new ConsoleAppender();
    private static final LogLevel level = initSettings();

    public static LogLevel getLogLevel() {
        return level;
    }

    public static LoggerGroup getLoggerGroup() {
        //   Class<?> clazz = //TODO получение класса где был вызван логгер.
        // if (clazz == null) return defaultGroup;
        //  return loggerGroups.computeIfAbsent(clazz, LoggerGroup::new);
        return null;
    }

    public static LoggerGroup getLoggerGroup(Appender appender) {
        if (appender == null) appender = defaultAppender;

    /*    Class<?> clazz = //TODO получение класса где был вызван логгер.
        if (clazz == null) return defaultGroup;
        return loggerGroups.computeIfAbsent(clazz, LoggerGroup::new);*/
        return null;
    }

    private static LogLevel initSettings() {
        return LogLevel.ALL;//TODO
    }
}
