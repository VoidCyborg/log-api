package ru.voidcyborg.logapi.logger;

import ru.voidcyborg.logapi.appender.Appender;
import ru.voidcyborg.logapi.appender.appenders.ConsoleAppender;
import ru.voidcyborg.logapi.level.LogLevel;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class LoggerFactory {

    private static final Map<String, LoggerGroup> loggerGroups = new ConcurrentHashMap<>();
    private static final Appender defaultAppender = new ConsoleAppender();
    private static final LoggerGroup defaultLoggerGroup = loggerGroups.computeIfAbsent("null", name -> new LoggerGroup(name, defaultAppender));

    private static final LogLevel level = initSettings();

    public static LogLevel getLogLevel() {
        return level;
    }

    public static LoggerGroup getDefaultLoggerGroup() {
        return defaultLoggerGroup;
    }

    public static LoggerGroup getLoggerGroup(String name) {
        if (name == null) return defaultLoggerGroup;
        return loggerGroups.computeIfAbsent(name, s -> {return new LoggerGroup(s,null)})
    }

    private static LogLevel initSettings() {
        return LogLevel.ALL;//TODO
    }


    public static String[] getClassMethodLine(int skip) {
        StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

        Optional<StackWalker.StackFrame> stackFrame = walker.walk(frames -> frames
                .skip(skip)
                .findFirst());

        return stackFrame.map(frame -> new String[]{frame.getFileName(), frame.getMethodName(), String.valueOf(frame.getLineNumber())}).
                orElse(new String[]{"#unknown", "#unknown", "-1"});
    }

    public static Class<?> getClass(int skip) {
        StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

        Optional<StackWalker.StackFrame> stackFrame = walker.walk(frames -> frames
                .skip(skip)
                .findFirst());

        return stackFrame.map(StackWalker.StackFrame::getDeclaringClass).orElse(null);
    }
}
