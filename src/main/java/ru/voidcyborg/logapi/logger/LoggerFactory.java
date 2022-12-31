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

    public synchronized static LoggerGroup getLoggerGroup(String name) throws NullPointerException {
        if (appenders == null || !initialized)
            throw new NullPointerException("Log API settings not initialized properly. Appenders is null");
        if (name == null) throw new NullPointerException("LoggerGroup name can't be null");
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

    /**
     * Создает уникальную новую группу логгирования без Appender'ов.
     * Группа создаётся с указанным Вами уровнем логгирования.
     * <p>
     * Creates a unique new logging group without Appenders.
     * The group is created with the logging level you specified.
     * <p>
     *
     * @param level Необходимый уровень логгирования. Required logging level.
     * @return Новую уникальную группу логгирования. New unique logging group.
     */
    public static LoggerGroup createCustomLoggerGroup(LogLevel level) {
        if (level == null) return new LoggerGroup(LoggerFactory.getDefaultLevel(), zone);
        return new LoggerGroup(level, zone);
    }

    /**
     * Возвращает стандартный уровень логгирования - INFO.
     * <p>
     * Return default logging level - INFO.
     * @return  Уровень логгирования. The logging level.
     */
    public static LogLevel getDefaultLevel() {
        return LogLevel.INFO;
    }


    /**
     * Данный метод возвращает класс, метод и строку в котором находится код в момент вызова данного метода.
     * Вы можете указать сколько элементов в стеке пропустить.
     * <p>
     * This method returns the class, the method and the line in which the code is located at the moment this method is called.
     * You can specify how many elements on the stack to skip.
     * <p>
     * <p>
     * Если не удалось найти класс, то возвращаю {@code new String[]{"#unknown", "#unknown", "-1"}}.
     * <p>
     * If the class could not be found, then I return {@code new String[]{"#unknown", "#unknown", "-1"}}.
     * <p>
     *
     * @param skip Количество элементов в стеке которое нужно пропустить. The number of elements on the stack to skip.
     * @return  Класс, метод и строку в котором вызван метод, в случае неудачи null. The class, the method and the line in which the method is called, {@code new String[]{"#unknown", "#unknown", "-1"}} on failure.
     */
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


    /**
     * Данный метод возвращает класс в котором находится код в момент вызова данного метода.
     * Вы можете указать сколько элементов в стеке пропустить.
     * <p>
     * This method returns the class in which the code is located at the moment this method is called.
     * You can specify how many elements on the stack to skip.
     * <p>
     * <p>
     * Если не удалось найти класс, то возвращаю null.
     * <p>
     * If the class could not be found, then I return null.
     * <p>
     *
     * @param skip Количество элементов в стеке которое нужно пропустить. The number of elements on the stack to skip.
     * @return  Класс в котором вызван метод, в случае неудачи null. The class in which the method is called, null on failure.
     */
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
