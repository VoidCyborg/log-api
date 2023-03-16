package ru.voidcyborg.logapi.appender.appenders;

import ru.voidcyborg.logapi.appender.Appender;

import java.util.Map;

/**
 * Данный класс необходим для того, чтобы прописать его в настройках системы если не нужно что-либо логгировать.
 * Методы данного класса ничего не делают и не выкидывают исключения.
 * <p>
 * <p>
 * This class is necessary in order to register it in the system settings if you do not need to log anything.
 * The methods of this class do nothing and do not throw exceptions.
 * <p>
 *
 * @author VoidCyborg
 * @see ru.voidcyborg.logapi.settings.Settings
 * @see ru.voidcyborg.logapi.appender.appenders.ConsoleAppender
 * @see ru.voidcyborg.logapi.appender.appenders.RollingFileAppender
 * @see ru.voidcyborg.logapi.logger.Logger
 * @see java.util.Map
 * @see java.lang.String
 */

public class VoidAppender implements Appender {


    /**
     * Данный метод предназначен для единоразовой передачи настроек.
     * В данном классе данный метод не выполняет какую-либо работу, так как класс не поддержкивает какие-либо настройки.
     * <p>
     * This method is intended for a one-time transfer of settings.
     * In this class, this method does not do any work, because the class does not support any customization.
     * <p>
     *
     * @param settings Карта строк-настроек {@code Map<String, String>}. Map of settings.
     */
    @Override
    public void parseSettings(Map<String, String> settings) {
    }

    /**
     * В данном классе данный метод не выполняет какую-либо работу,
     * так как класс не поддержкивает какое-либо логгирование.
     * <p>
     * In this class, this method does not do any work,
     * since the class does not support any logging.
     * <p>
     *
     * @param text Значение {@code String} которое необходимо записать.<p>
     *             The {@code String} value to write.
     * @return Если строка не null и не пустая возвращает true. В остальных случаях false.
     * <p> If the string is not null and not empty, returns true. Otherwise, false.
     */
    @Override
    public boolean append(String text) {
        return text != null && !text.isEmpty();
    }

    /**
     * Ничего не делает.
     */
    @Override
    public void destroy() {
    }
}
