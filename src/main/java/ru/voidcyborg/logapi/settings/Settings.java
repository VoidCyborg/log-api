package ru.voidcyborg.logapi.settings;

import ru.voidcyborg.logapi.appender.Appender;
import ru.voidcyborg.logapi.level.LogLevel;

import java.util.*;


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
 * Сами настройки можно получить используя метод {@code HashMap<String, String> getAppenderSettings();}.
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
    private final Appender[] appenders;
    private final HashMap<String, String> appenderSettings = new HashMap<>();

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
        this.parseAppenderSettings();

        try {
            for (Appender appender : appenders) {
                appender.parseSettings(this.getAppenderSettings());
            }
        } catch (Exception e) {
            throw new SettingsInitException("Failed to init settings in Appenders. " + e);
        }
    }

    /**
     * Создаётся копия карты настроек, для того чтобы изначальная карта не была изменена.
     * <p>
     * A copy of the settings map is created so that the original map is not changed.
     *
     * @return Карту с содержанием всех настроек которые были переданы объекту. <p> A map containing all the settings that were passed to the object.
     */
    @SuppressWarnings("unchecked")
    public HashMap<String, String> getAppenderSettings() {
        return (HashMap<String, String>) appenderSettings.clone();
    }

    /**
     * Создаётся копия Appender'ов которые были созданы основываясь на указанных настроках.
     * <p>
     * Creates a copy of Appenders that were created based on the specified settings.
     *
     * @return Копию массива Appender'ов. <p> A copy of the Appender array.
     */
    public Appender[] getAppenders() {
        return Arrays.copyOf(appenders, appenders.length);
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
        LogLevel level;
        for (String line : args) {
            try {
                if (!line.startsWith("LogLevel=")) continue;
                line = line.replace("LogLevel=", "");
                level = LogLevel.valueOf(line);

                return level;
            } catch (Exception ignore) {
            }
        }

        throw new SettingsInitException("Failed to find LogLevel in settings");
    }

    //Прохожусь по строкам и ищу упоминания TimeZone= и пытаюсь получить временную зону.
    //Если указанно несколько значений то будет взято первое.
    //Если значение будет указанно не корректно то будет взято GMT.
    private TimeZone parseZone() throws SettingsInitException {
        TimeZone zone;
        for (String line : args) {
            try {
                if (!line.startsWith("TimeZone=")) continue;
                line = line.replace("TimeZone=", "");
                zone = TimeZone.getTimeZone(line);

                return zone;
            } catch (Exception ignore) {
            }
        }

        throw new SettingsInitException("Failed to find TimeZone in settings");
    }

    //Прохожусь по строкам и ищу упоминания appender= и пытаюсь получить путь к классу Appender'а.
    //Все указанные Appender'ы будут созданы. Если не удастся создать хоть один, то выкидываю ошибку.
    private Appender[] createAppenders() throws SettingsInitException {
        List<String> paths = new ArrayList<>();
        for (String s : this.args) {
            if (s == null) continue;
            if (!s.startsWith("appender=")) continue;
            String[] parts = s.split("=");
            if (parts.length != 2) throw new SettingsInitException("Wrong settings syntax:" + s);

            paths.add(parts[1]);
        }

        if (paths.isEmpty()) throw new SettingsInitException("No single path to the Appender class.");

        List<Appender> appenders = new ArrayList<>();
        for (String s : paths) {
            try {
                Appender appender = (Appender) Class.forName(s).getDeclaredConstructor().newInstance();
                appenders.add(appender);
            } catch (Exception e) {
                throw new SettingsInitException("Failed to create an Appender:" + s);
            }
        }

        return appenders.toArray(new Appender[0]);
    }

    //Прохожусь по строкам и ищу упоминания appender. и создаю набор пар ключ-значение. Пример: appender.key=value
    //Все указанные настройки будут применены ко всем Appender'ам.
    //Поэтому при создании конфига не рекомендуется создавать более 1 Appender'а 1 класса.
    //Или с общими именами настроек.
    //При любом нарушении синтаксиса выкидывает ошибку.
    private void parseAppenderSettings() throws SettingsInitException {
        String[] parts;
        String prefix = "appender.";
        try {
            for (String s : args) {
                parts = s.split("=");
                if (parts.length != 2) throw new SettingsInitException("Wrong settings syntax:" + s);
                if (!(parts[0].startsWith(prefix))) continue;

                this.appenderSettings.put(parts[0].substring(prefix.length()), parts[1]);
            }
        } catch (Exception e) {
            throw new SettingsInitException("Wrong setting syntax:" + e);
        }
    }
}
