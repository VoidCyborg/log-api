package ru.voidcyborg.logapi.logger;

import ru.voidcyborg.logapi.appender.Appender;
import ru.voidcyborg.logapi.appender.appenders.ConsoleAppender;
import ru.voidcyborg.logapi.level.LogLevel;
import ru.voidcyborg.logapi.settings.Settings;
import ru.voidcyborg.logapi.settings.SettingsInitException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class LoggerFactory {

    private static final Map<String, LoggerGroup> loggerGroups = new ConcurrentHashMap<>();
    private static final LoggerGroup defaultLoggerGroup = loggerGroups.computeIfAbsent("null", name -> new LoggerGroup());
    private static final Appender defaultAppender = new ConsoleAppender();

    private static volatile Appender appender;
    private static volatile LogLevel level;


    public static void setSettings(String path, boolean resources) throws SettingsInitException {
        if (path == null) throw new SettingsInitException("Path to settings can't be null");

        List<String> lines = new ArrayList<>();
        if (resources) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(LoggerFactory.class.getResourceAsStream(path))))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            } catch (Exception e) {
                throw new SettingsInitException(e.toString());
            }
        } else {


        }
        Settings settings = new Settings(lines.toArray(new String[0]));


    }


    public static LogLevel getLogLevel() throws NullPointerException {
        if (level == null)
            throw new NullPointerException("Log API settings not initialized properly. LogLevel is null");
        return level;
    }

    public static LoggerGroup getLoggerGroup(String name) throws NullPointerException {
        if (appender == null)
            throw new NullPointerException("Log API settings not initialized properly. Appender is null");
        if (name == null) throw new NullPointerException("Logger Group name can't be null");
        return loggerGroups.computeIfAbsent(name, s -> new LoggerGroup().addAppender(appender));
    }

    public static LoggerGroup createCustomLoggerGroup() {
        return new LoggerGroup();
    }


    public static Appender getDefaultAppender() {
        return defaultAppender;
    }

    public static LogLevel getDefaultLevel() {
        return LogLevel.WARN;
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
