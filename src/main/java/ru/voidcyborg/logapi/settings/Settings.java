package ru.voidcyborg.logapi.settings;

import ru.voidcyborg.logapi.appender.Appender;
import ru.voidcyborg.logapi.level.LogLevel;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


/**
 * Класс {@code Settings} предствляет из себя объект с парами ключ значение за счёт {@code Map<String,String>}.
 * <p>
 * The class {@code Settings} is an object with key-value pairs due to {@code Map<String,String>}.
 * <p>
 * <p>
 * Для создания экземпляра необходимо передать массив строк которые содержат пары ключ значение с '=' в качестве разделителя.
 * <p>
 * To create an instance, you must pass an array of strings that contain key-value pairs with '=' as the delimiter.
 * <blockquote><pre>
 *     String[] args = new String[]{"key1=value1", "key2=value2"};
 *     try{
 *          Settings settings = new Settings(args);
 *     }catch(SettingsInitException e){
 *         //Wrong settings syntax
 *     }
 * </pre></blockquote><p>
 * При создании настроек создаются указанные Appender'ы и к ним применяются данные настройки.
 * Их можно получить использую метод {@code Appender[] getAppenders();}.
 * <p>
 * When creating settings, the specified Appenders are created and these settings are applied to them.
 * They can be obtained using the {@code Appender[] getAppenders();} method.
 * <p>
 * Сами настройки можно получить используя метод {@code HashMap<String, String> getAppenderSettings(AppenderName);}.
 * <p>
 * The settings themselves can be obtained using the {@code HashMap<String, String> getAppenderSettings();} method.
 * <p>
 *
 * @author VoidCyborg
 * @see ru.voidcyborg.logapi.logger.LoggerFactory
 * @see ru.voidcyborg.logapi.logger.Logger
 * @see ru.voidcyborg.logapi.appender.Appender
 * @see ru.voidcyborg.logapi.settings.SettingsInitException
 * @see java.util.Map
 * @see java.lang.String
 */
public final class Settings {

    private final String[] args;
    private final LogLevel level;
    private final TimeZone zone;
    private final HashMap<String, Appender> appenders;
    private final HashMap<String, HashMap<String, String>> appenderSettings;

    /**
     * Конструктор данного класа требует передачи пар ключ-значение с разделителем в виде '='.
     * Массив не может быть null, любая строка в массиве также не может быть null.
     * <p>
     * The constructor of this class requires passing key-value pairs with a delimiter of the form '='.
     * An array cannot be null, any string in an array cannot be null either.
     * <p>
     * В случае любого нарушения синтаксиса будет выбрашено исключение {@code SettingsInitException}.
     * <p>
     * Any syntax violation will throw {@code SettingsInitException}.
     */
    public Settings(String[] args) throws SettingsInitException {
        if (args == null) throw new SettingsInitException("Parameters can't be null");
        this.args = args.clone();
        this.level = parseLevel();
        this.zone = parseZone();
        this.appenders = createAppenders();
        this.appenderSettings = parseAppenderSettings();

        try {
            for (Map.Entry<String, Appender> entry : appenders.entrySet()) {
                entry.getValue().parseSettings(this.appenderSettings.get(entry.getKey()));
            }
        } catch (Exception e) {
            throw new SettingsInitException("Failed to init settings in Appenders. " + e);
        }
    }

    /**
     * Создаётся копия карты настроек для данного appender'а, для того чтобы изначальная карта не была изменена.
     * <p>
     * A copy of the settings map is created so that the original map is not changed.
     *
     * @return Карту с содержанием всех настроек которые были переданы объекту. <p> A map containing all the settings that were passed to the object.
     */
    @SuppressWarnings("unchecked")
    public HashMap<String, String> getAppenderSettings(String appenderName) {//TODO
        if (appenderName == null) return null;

        HashMap<String, String> result = this.appenderSettings.get(appenderName);
        if (result != null) return (HashMap<String, String>) result.clone();

        return null;
    }


    /**
     * Создаётся копия имён Appender'ов которые были созданы основываясь на указанных настроках.
     * <p>
     * Creates a copy of Appender Names that were created based on the specified settings.
     *
     * @return Копию массива имён Appender'ов. <p> A copy of the Appender Names array.
     */
    public String[] getAppenderNames() {
        return this.appenders.keySet().toArray(new String[0]);
    }

    /**
     * Создаётся копия Appender'ов которые были созданы основываясь на указанных настроках.
     * <p>
     * Creates a copy of Appenders that were created based on the specified settings.
     *
     * @return Копию массива Appender'ов. <p> A copy of the Appender array.
     */
    public Appender[] getAppenders() {
        return appenders.values().toArray(new Appender[0]);
    }

    /**
     * Возвращает уровень логгирования указанный в настройках.
     * <p>
     * Returns the logging level specified in the settings.
     *
     * @return Уровень логгирования {@code LogLevel}. <p> Logging level {@code LogLevel}.
     */
    public LogLevel getLevel() {
        return level;
    }

    /**
     * Возвращает часовой пояс указанный в настройках.
     * <p>
     * Returns the time zone specified in the settings.
     *
     * @return Часовой пояс {@code TimeZone}. <p> Time zone {@code TimeZone}.
     */
    public TimeZone getTimeZone() {
        return zone;
    }


    //Прохожусь по строкам и ищу упоминания LogLevel= и пытаюсь получить уровень.
    //Если указанно несколько значений то будет взято первое попавшееся.
    private LogLevel parseLevel() throws SettingsInitException {
        try {
            LogLevel level;
            for (String line : args) {
                if (line == null) continue;
                try {
                    line = line.replace(" ", "");
                    if (!line.startsWith("LogLevel=")) continue;
                    line = line.replace("LogLevel=", "");
                    level = LogLevel.valueOf(line);

                    return level;
                } catch (Exception ignore) {
                }
            }
        } catch (Exception e) {
            throw new SettingsInitException("Failed to find LogLevel in settings, because of " + e);
        }

        throw new SettingsInitException("Failed to find LogLevel in settings");
    }

    //Прохожусь по строкам и ищу упоминания TimeZone= и пытаюсь получить временную зону.
    //Если указанно несколько значений то будет взято первое.
    //Если значение будет указанно не корректно то будет взято GMT.
    private TimeZone parseZone() throws SettingsInitException {
        try {
            TimeZone zone;
            for (String line : args) {
                if (line == null) continue;
                try {
                    line = line.replace(" ", "");
                    if (!line.startsWith("TimeZone=")) continue;
                    line = line.replace("TimeZone=", "");
                    zone = TimeZone.getTimeZone(line);

                    return zone;
                } catch (Exception ignore) {
                }
            }
        } catch (Exception e) {
            throw new SettingsInitException("Failed to find TimeZone in settings, because of " + e);
        }

        throw new SettingsInitException("Failed to find TimeZone in settings");
    }

    //Прохожусь по строкам и ищу упоминания appender*= и пытаюсь получить путь к классу Appender'а.
    //Все указанные Appender'ы будут созданы. Если не удастся создать хоть один, то выкидываю ошибку.
    //Если встречаю дубликаты выкидываю ошибку.
    private HashMap<String, Appender> createAppenders() throws SettingsInitException {
        HashMap<String, Appender> appenders = new HashMap<>();

        try {
            String prefix = "appender";

            for (String s : this.args) {
                if (s == null) continue;
                s = s.replace(" ", "");
                if (!s.startsWith(prefix)) continue;
                String[] parts = s.split("=");
                if (parts.length != 2) throw new SettingsInitException("Wrong settings syntax: " + s);
                if (parts[0].contains(".")) continue;
                if (appenders.containsKey(parts[0]))
                    throw new SettingsInitException("Appender already initialized: " + s);

                try {
                    Appender appender = (Appender) Class.forName(parts[1]).getDeclaredConstructor().newInstance();
                    appenders.put(parts[0], appender);
                } catch (Exception e) {
                    throw new SettingsInitException("Failed to create an Appender: " + s);
                }
            }
        } catch (Exception e) {
            if (e instanceof SettingsInitException) throw e;
            throw new SettingsInitException("Wrong settings because of " + e);
        }

        if (appenders.isEmpty()) throw new SettingsInitException("No single path to the Appender class.");

        return appenders;
    }

    //Прохожусь по строкам и ищу упоминания appender*. и создаю набор пар ключ-значение. Пример: appender1.key=value или appender.key=value
    //При любом нарушении синтаксиса выкидывает ошибку.
    private HashMap<String, HashMap<String, String>> parseAppenderSettings() throws SettingsInitException {
        HashMap<String, HashMap<String, String>> appenderSettings = new HashMap<>();

        String[] parts;

        try {
            for (String name : appenders.keySet()) {
                HashMap<String, String> settings = new HashMap<>();

                for (String s : args) {
                    if (!s.startsWith(name)) continue;
                    parts = s.split("=");
                    if (parts.length != 2) throw new SettingsInitException("Wrong settings syntax:" + s);
                    if (!parts[0].replace(name, "").startsWith(".")) continue;

                    settings.put(parts[0].substring(name.length() + 1), parts[1]);
                }
                appenderSettings.put(name, settings);
            }
        } catch (Exception e) {
            throw new SettingsInitException("Wrong setting syntax:" + e);
        }

        return appenderSettings;
    }
}
