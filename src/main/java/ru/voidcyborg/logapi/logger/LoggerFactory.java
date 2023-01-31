package ru.voidcyborg.logapi.logger;

import ru.voidcyborg.logapi.appender.Appender;
import ru.voidcyborg.logapi.level.LogLevel;
import ru.voidcyborg.logapi.settings.Settings;
import ru.voidcyborg.logapi.settings.SettingsInitException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Класс {@code LoggerFactory} предствляет из себя объект, который позволяет создавать LoggerGroup'ы.
 * <p>
 * <p>
 * The {@code LoggerFactory} class is an object that allows you to create LoggerGroups.
 * <p>
 * <p>
 * Для создания группы логгирования необходимо инициализировать настройки системы логгирования.
 * Пример инициализации настроек:
 * <p>
 * <p>
 * To create a logging group, you need to initialize the logging system settings.
 * Example of settings initialization:
 * <blockquote><pre>
 *
 * public void methodA(){
 *    try{
 *      String path = "/assets/project/logger.settings";
 *      LoggerFactory.setSettings(CurrentClass.class.getResourceAsStream(path));
 *    }catch(Exception e){
 *        System.exit(0);
 *    }
 * }
 *
 * </pre></blockquote><p>
 * Нельзя задавать настройки более 1 раза.
 * При попытке переопределить их будет выбрашена ошибка.
 * <p>
 * You cannot set the settings more than once.
 * If you try to override them, an error will be thrown.
 * <p>
 *
 * @author VoidCyborg
 * @see ru.voidcyborg.logapi.level.LogLevel
 * @see ru.voidcyborg.logapi.appender.Appender
 * @see ru.voidcyborg.logapi.logger.Logger
 * @see ru.voidcyborg.logapi.logger.LoggerGroup
 * @see java.text.SimpleDateFormat
 * @see java.util.Map
 * @see java.lang.String
 */
public final class LoggerFactory {

    private static final Map<String, LoggerGroup> loggerGroups = new ConcurrentHashMap<>();
    private static volatile Appender[] appenders;
    private static volatile Settings settings;
    private static volatile LogLevel level;
    private static volatile TimeZone zone = TimeZone.getTimeZone("Europe/Moscow");//Нужно, чтобы мог работать метод createCustomLoggerGroup(LogLevel level);
    private static volatile boolean initialized;


    /**
     * Инициализирует настройки для системы логгирования.
     * Есть возможность получить их как из внешнего файла, так и из файла приложения.
     * <p>
     * Initializes settings for the logging system.
     * It is possible to get them both from an external file and from an application file.
     * <p>
     * При неудаче или некорректных настройках в файле выкидывает ошибку {@code SettingsInitException}.
     * <p>Throws an error {@code SettingsInitException} on failure or incorrect settings in the file.
     * <p>
     * При повторном вызове после корректной инициализации будет выбрашена ошибка!
     * <p>
     * When called again after correct initialization, an error will be thrown!
     *
     * @param path Путь к файлу. The path to the file.
     */
    public synchronized static void setSettings(String path) throws SettingsInitException {
        if (path == null) throw new SettingsInitException("Path to settings can't be null");
        if (initialized) throw new SettingsInitException("Settings is already initialized!");

        Settings parsedSettings = parseSettings(path, null);

        appenders = parsedSettings.getAppenders();
        level = parsedSettings.getLevel();
        zone = parsedSettings.getTimeZone();
        settings = parsedSettings;
        initialized = true;
    }

    /**
     * Инициализирует настройки для системы логгирования.
     * Есть возможность получить их как из внешнего файла, так и из файла приложения.
     * <p>
     * Initializes settings for the logging system.
     * It is possible to get them both from an external file and from an application file.
     * <p>
     * При неудаче или некорректных настройках в файле выкидывает ошибку {@code SettingsInitException}.
     * <p>Throws an error {@code SettingsInitException} on failure or incorrect settings in the file.
     * <p>
     * При повторном вызове после корректной инициализации будет выбрашена ошибка!
     * <p>
     * When called again after correct initialization, an error will be thrown!
     *
     * @param stream Стрим к файлу с настройками. InputStream to file with settings
     */
    public synchronized static void setSettings(InputStream stream) throws SettingsInitException {
        if (stream == null) throw new SettingsInitException("stream to settings can't be null");
        if (initialized) throw new SettingsInitException("Settings is already initialized!");

        Settings parsedSettings = parseSettings(null, stream);

        appenders = parsedSettings.getAppenders();
        level = parsedSettings.getLevel();
        zone = parsedSettings.getTimeZone();
        settings = parsedSettings;
        initialized = true;
    }

    /**
     * Возвращает настрокйи которые были получены из файла настроек.
     * <p>
     * Returns the settings that were received from the settings file.
     * <p>
     * Если настройки не инициализированы, то выкинет ошибку.
     * <p>
     * If the settings are not initialized, it will throw an error.
     *
     * @return Настройки системы логгирования. Logging system settings.
     * @throws ru.voidcyborg.logapi.settings.SettingsInitException
     */
    public synchronized static Settings getSettings() throws SettingsInitException {
        if (settings == null || !initialized)
            throw new SettingsInitException("Log API settings not initialized properly. Settings is null");
        return settings;
    }


    /**
     * Возвращает уровень логгирования который был получен из файла настроек.
     * <p>
     * Returns the logging level that was obtained from the settings file.
     * <p>
     * Если настройки не инициализированы, то выкинет ошибку.
     * <p>
     * If the settings are not initialized, it will throw an error.
     *
     * @return Уровень логгирования из файла. Logging level from a file.
     * @throws ru.voidcyborg.logapi.settings.SettingsInitException
     */
    public synchronized static LogLevel getLogLevel() throws SettingsInitException {
        if (level == null || !initialized)
            throw new SettingsInitException("Log API settings not initialized properly. LogLevel is null");
        return level;
    }

    /**
     * Возвращает группу логгирования с определённым именем.
     * Группа создаётся с уровнем логгирования полученым из файла настроек.
     * <p>
     * Returns the logging group with the specified name.
     * The group is created with the logging level obtained from the settings file.
     * <p>
     * Если настройки не инициализированы, то выкинет ошибку.
     * <p>
     * If the settings are not initialized, it will throw an error.
     *
     * @param name Имя для группы логгирования. Name for the logging group.
     * @return Группу логгирования с настройками из файла. Logging group with settings from a file.
     * @throws java.lang.NullPointerException
     */
    public synchronized static LoggerGroup getLoggerGroup(String name) throws NullPointerException {
        if (appenders == null || !initialized)
            throw new NullPointerException("Log API settings not initialized properly. Appenders is null");
        if (name == null) throw new NullPointerException("LoggerGroup name can't be null");
        return loggerGroups.computeIfAbsent(name, s -> new LoggerGroup(level, zone).addAppenders(appenders));
    }

    /**
     * Возвращает объект с настройками системы логгирования из файла.
     * Есть возможность получить его как из внешнего файла, так и из файла приложения.
     * <p>
     * Returns an object with logging system settings from a file.
     * It is possible to get it both from an external file and from an application file.
     * <p>
     * При неудаче или некорректных настройках в файле выкидывает ошибку {@code SettingsInitException}.
     * <p>Throws an error {@code SettingsInitException} on failure or incorrect settings in the file.
     *
     * @param path   Путь к файлу. The path to the file.
     * @param stream Входящий стрим к файлу. Input stream to the settings file.
     * @return Настройки для системы логгирования. Settings for the logging system.
     */
    public static Settings parseSettings(String path, InputStream stream) throws SettingsInitException {
        if (path == null && stream == null)
            throw new SettingsInitException("Path to settings and input stream can't be null");

        List<String> lines = new ArrayList<>();
        if (stream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
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
     *
     * @return Уровень логгирования. The logging level.
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
     * @return Класс, метод и строку в котором вызван метод, в случае неудачи null. The class, the method and the line in which the method is called, {@code new String[]{"#unknown", "#unknown", "-1"}} on failure.
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
     * @return Класс в котором вызван метод, в случае неудачи null. The class in which the method is called, null on failure.
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
