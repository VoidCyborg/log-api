package ru.voidcyborg.logapi.appender;

import ru.voidcyborg.logapi.settings.SettingsInitException;

import java.util.Map;

/**
 * Данный интерфейс необходим для использования в системе логирования через него идёт запись данных в логи.
 * У пользователей данной библиотеки есть возможность написать и использовать свою реализацию данного интерфейса.
 * <p>
 *
 * This interface is required for use in the logging system through which data is written to the logs.
 * Users of this library have the opportunity to write and use their own implementation of this interface.
 * <p>
 *
 * Рекомендуется разрабатывать реализацию данного интерфейса с учётом того, что методы могут быть вызваны из разных потоков.
 * Также рекомендуется не инициализировать настройки более одного раза.
 *<p>
 *
 * It is recommended to develop an implementation of this interface, taking into account the fact that methods can be called from different threads.
 * It is also recommended not to initialize settings more than once.
 * <p>
 *
 * Класс должен иметь конструктор без параметров. Так как в дальнейшем будут создаваться экземпляры класса в Settings.
 * <p>
 *
 * The class must have a parameterless constructor. Since in the future instances of the class will be created in Settings.
 * <p>
 *
 * @author  VoidCyborg
 * @see     ru.voidcyborg.logapi.settings.Settings
 * @see     ru.voidcyborg.logapi.appender.appenders.ConsoleAppender
 * @see     ru.voidcyborg.logapi.appender.appenders.RollingFileAppender
 * @see     ru.voidcyborg.logapi.logger.Logger
 * @see     java.util.Map
 * @see     java.lang.String
 */
public interface Appender {

    /**
     * Данный метод предназначен для единоразовой передачи настроек.
     * При неокоренных данных настроек необходимо выкинуть исключение с деталями ошибки. Рекомендуется использовать {@code SettingsInitException}.
     * <p>
     * This method is intended for a one-time transfer of settings.
     * If these settings are incorrect, it is necessary to throw an exception with the details of the error. {@code SettingsInitException} is recommended.
     * <p>
     *
     * @param   settings Карта строк-настроек {@code Map<String, String>}. Map of settings.
     * @throws  ru.voidcyborg.logapi.settings.SettingsInitException
     *
     */
    void parseSettings(Map<String, String> settings) throws SettingsInitException;


    /**
     * Данный метод предназначен для записи строки.
     * Рекомендуется возвращать {@code false} если строка {@code text == null} или {@code text.isEmpty() == true}.
     * <p>
     * This method is for appending a string.
     * Recommended to return {@code false} if string {@code text == null} or {@code text.isEmpty == true}.
     * <p>
     *
     * Не должно быть выбрашено каких-либо исключений.
     * <p>
     * No exceptions should be thrown.
     * <p>
     *
     * Также, рекомендуется реализовывать неблокирующее исполнение данного метода(например за счёт {@code Executor}).
     * <p>
     * Also, it is recommended to implement non-blocking execution of this method (for example, due to {@code Executor}).
     *
     * @param   text Строка которую необходимо записать. The string to be appended.
     *
     */
    boolean append(String text);
}
