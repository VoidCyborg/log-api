package ru.voidcyborg.logapi.settings;

/**
 * Класс {@code SettingsInitException} предствляет из себя ошибку инициализации настроек.
 * <p>
 * The {@code SettingsInitException} class represents an error initializing the settings.
 * <p>
 *
 * @author  VoidCyborg
 * @see     ru.voidcyborg.logapi.settings.Settings
 * @see     ru.voidcyborg.logapi.logger.LoggerFactory
 * @see     java.lang.String
 */
public final class SettingsInitException extends Exception {

    /**
     * Реализую стандартный конструктор для класса {@code Exception}.
     * <p>
     * Implement the standard constructor for the class {@code Exception}.
     */
    public SettingsInitException() {
        super();
    }

    /**
     * Реализую стандартный конструктор для класса {@code Exception}.
     * Рекомендуется не передавать null значение.
     * <p>
     * Implement the standard constructor for the class {@code Exception}.
     * It is recommended not to pass null value.
     */
    public SettingsInitException(String s) {
        super(s);
    }
}
