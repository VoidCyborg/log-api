package ru.voidcyborg.logapi.logger;

import ru.voidcyborg.logapi.appender.Appender;
import ru.voidcyborg.logapi.level.LogLevel;
import ru.voidcyborg.logapi.settings.Settings;
import ru.voidcyborg.logapi.settings.SettingsInitException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class LoggerFactory {

    private static final Map<String, LoggerGroup> loggerGroups = new ConcurrentHashMap<>();
    private static volatile Appender[] appenders;
    private static volatile Settings settings;
    private static volatile LogLevel level;
    private static volatile TimeZone zone = TimeZone.getTimeZone("Europe/Moscow");
    private static volatile boolean initialized;


    public synchronized static void setSettings(String path, boolean resources) throws SettingsInitException {
        if (path == null) throw new SettingsInitException("Path to settings can't be null");
        if (initialized) throw new SettingsInitException("Settings is already initialized!");

        Settings parsedSettings = parseSettings(path, resources);

        appenders = parsedSettings.getAppenders();
        level = parsedSettings.getLevel();
        zone = parsedSettings.getTimeZone();
        settings = parsedSettings;
        initialized = true;
    }

    public synchronized static Settings getSettings() throws SettingsInitException {
        if (settings == null || !initialized)
            throw new SettingsInitException("Log API settings not initialized properly. Settings is null");
        return settings;
    }


    public synchronized static LogLevel getLogLevel() throws SettingsInitException {
        if (level == null || !initialized)
            throw new SettingsInitException("Log API settings not initialized properly. LogLevel is null");
        return level;
    }

    public synchronized static LoggerGroup getLoggerGroup(String name) throws SettingsInitException {
        if (appenders == null || !initialized)
            throw new SettingsInitException("Log API settings not initialized properly. Appenders is null");
        if (name == null) throw new SettingsInitException("LoggerGroup name can't be null");
        return loggerGroups.computeIfAbsent(name, s -> new LoggerGroup(level, zone).addAppenders(appenders));
    }

    public static Settings parseSettings(String path, boolean resources) throws SettingsInitException {
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
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            } catch (Exception e) {
                throw new SettingsInitException(e.toString());
            }
        }

        return new Settings(lines.toArray(new String[0]));
    }

    public static LoggerGroup createCustomLoggerGroup(LogLevel level) {
        if (level == null) return new LoggerGroup(LoggerFactory.getDefaultLevel(), zone);
        return new LoggerGroup(level, zone);
    }

    public static LogLevel getDefaultLevel() {
        return LogLevel.INFO;
    }


    public static String[] getClassMethodLine(int skip) {
        try {
            StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

            Optional<StackWalker.StackFrame> stackFrame = walker.walk(frames -> frames
                    .skip(skip)
                    .findFirst());

            return stackFrame.map(frame -> new String[]{frame.getFileName(), frame.getMethodName(), String.valueOf(frame.getLineNumber())}).
                    orElse(new String[]{"#unknown", "#unknown", "-1"});
        } catch (Exception e) {
            return new String[]{"#unknown", "#unknown", "-1"};
        }
    }

    public static Class<?> getClass(int skip) {
        try {
            StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

            Optional<StackWalker.StackFrame> stackFrame = walker.walk(frames -> frames
                    .skip(skip)
                    .findFirst());

            return stackFrame.map(StackWalker.StackFrame::getDeclaringClass).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}
