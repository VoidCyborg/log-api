package ru.voidcyborg.logapi.appender.appenders;

import ru.voidcyborg.logapi.appender.Appender;
import ru.voidcyborg.logapi.settings.SettingsInitException;

import java.util.Map;

/**
 * Класс {@code СonsoleAppender} предствляет из себя реализацию записи строк в консоль.
 * Его цель передавать строку в системную консоль.
 * <p>
 *
 * The {@code СonsoleAppender} class represents the implementation of writing strings to the console.
 * It's purpose is to pass a string to the system console.
 * <p>
 *
 * Для записи необходимо вызвать метод {@code append(String s)}.
 * Пример использования:
 *<p>
 *
 * To write, you must call the {@code append(String s)} method.
 * Usage example:
 * <blockquote><pre>
 *     String str = "abc";
 *     Appender appender = new ConsoleAppender();
 *     appender.append(str);
 * </pre></blockquote><p>
 * Метод {@code parseSettings(HashMap<String, String> settings)} в данном классе ничего не делает.
 * Данный класс не поддерживает, какие-либо настройки.
 * <p>
 * Method {@code parseSettings(HashMap<String, String> settings)} does nothing in this class.
 * This class does not support any settings.
 * <p>
 *
 * @author  VoidCyborg
 * @see     ru.voidcyborg.logapi.appender.Appender
 * @see     ru.voidcyborg.logapi.logger.Logger
 * @see     java.util.Map
 * @see     java.lang.String
 */
public final class ConsoleAppender implements Appender {

    /**
     * Данный метод предназначен для единоразовой передачи настроек.
     * В данном классе данный метод не выполняет какую-либо работу, так как класс не поддержкивает какие-либо настройки.
     * <p>
     * This method is intended for a one-time transfer of settings.
     * In this class, this method does not do any work, because the class does not support any customization.
     * <p>
     *
     * @param   settings Карта строк-настроек {@code Map<String, String>}. Map of settings.
     */
    @Override
    public void parseSettings(Map<String, String> settings) {
    }

    /**
     * Данный метод предназначен для записи {@code String} в консоль.
     * Метод проверяет строку на то не {@code null} ли она и не пустая ли она. И далее передаёт её консоли.
     * <p>
     * This method is designed to write a {@code String} to the console.
     * The method checks if the string is not {@code null} or empty. And then passes it to the console.
     * <p>
     *
     * @param   text Значение {@code String} которое необходимо записать в консоль.<p>
     *              The {@code String} value to write to the console
     * @return Если строка была передана в косоль то возвращается true. В остальных случаях false. <p>
     *     If a string was passed to the console, then true is returned. Otherwise, false.
     *
     */
    @Override
    public boolean append(String text) {
        if(text == null || text.isEmpty()) return false;
        System.out.print(text);
        return true;
    }
}
