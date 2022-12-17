package ru.voidcyborg.logapi.level;

/**
 * Данный enum предназначен для регулировки уровня логирования.
 * Он состоит из 8 значений от ALL до OFF.
 * <p>
 *
 * This enum is designed to adjust the logging level.
 * It consists of 8 values from ALL to OFF.
 * <p>
 *
 *
 * @author  VoidCyborg
 * @see     ru.voidcyborg.logapi.logger.LoggerFactory
 * @see     ru.voidcyborg.logapi.logger.LoggerGroup
 * @see     ru.voidcyborg.logapi.logger.Logger
 * @see     java.util.Map
 * @see     java.lang.String
 */
public enum LogLevel {
    ALL,
    TRACE,
    DEBUG,
    INFO,
    WARN,
    ERROR,
    FATAL,
    OFF;


    //Храню посчитаную строку, чтобы не считать её каждый раз при вызове .toString();
    private final String toString = calcString();

    /**
     * Данный метод предназначен для представления уровня в виде строки.
     * Если размер строки менее 5 символов, то добавляются пробелы.
     * Это необходимо для того, чтобы уровень занимал одинаковую длинну в логах.
     * <p>
     * This method is designed to represent the level as a string.
     * If the string is less than 5 characters, spaces are added.
     * This is necessary so that the level takes the same length in the logs.
     * <p>
     *
     * @return Возвращает строку длинной 5. Returns a string with a length of 5.
     *
     */
    @Override
    public String toString() {
        return toString;
    }

    //Высчитывает строку из имени + пробелы
    private String calcString() {
        StringBuilder result = new StringBuilder(this.name());
        if (result.length() == 5) return result.toString();
        for (int i = 0; i < 5 - result.length(); i++) {
            result.append(' ');
        }
        return result.toString();
    }


}
