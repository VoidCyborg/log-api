package ru.voidcyborg.logapi.logger;

import ru.voidcyborg.logapi.appender.Appender;
import ru.voidcyborg.logapi.level.LogLevel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;

/**
 * Класс {@code Logger} предствляет из себя объект, который передаёт строки и объекты Appender'ам, которые уже их записывают.
 * <p>
 * <p>
 * The {@code Logger} class is an object that passes strings and objects to Appenders that are already logging them.
 * <p>
 * <p>
 * Для записи необходимо вызвать метод с нужным уровнем логгирования. {@code info(String s) or fatal(String s, Object obj)}.
 * Пример использования:
 * <p>
 * <p>
 * To write, you need to call a method with the desired logging level. {@code info(String s) or fatal(String s, Object obj)}.
 * Usage example:
 * <blockquote><pre>
 *     String str = "abc";
 *     private static final Logger logger = LoggerFactory.getLoggerGroup(String name).getLogger();
 *
 *     public void methodA(){
 *        logger.info(str);
 *        logger.fatal("Error because of ", str);
 *     }
 * </pre></blockquote><p>
 * Экземпляры данного класса может создать только {@code LoggerGroup}.
 * Не рекомендуется передавать объект {@code Logger} между другими объектами и классами.
 * <p>
 * Instances of this class can only be created by {@code LoggerGroup}.
 * It is not recommended to pass the {@code Logger} object between other objects and classes.
 * <p>
 *
 * @author VoidCyborg
 * @see ru.voidcyborg.logapi.level.LogLevel
 * @see ru.voidcyborg.logapi.appender.Appender
 * @see ru.voidcyborg.logapi.logger.LoggerGroup
 * @see java.text.SimpleDateFormat
 * @see java.util.Map
 * @see java.lang.String
 */
public final class Logger {

    private final SimpleDateFormat date = new SimpleDateFormat("[dd-MM-yyyy][HH:mm:ss]");
    private final Set<Appender> appenders;
    private final LogLevel level;
    private final TimeZone zone;

    private final boolean fatal;
    private final boolean error;
    private final boolean warn;
    private final boolean info;
    private final boolean debug;
    private final boolean trace;

    //Может быть вызвано только в LoggerGroup. Set<Appender> не будет редактироваться, нужен только для чтения.
    Logger(Set<Appender> appenders, LogLevel level, TimeZone zone) {
        this.appenders = appenders;
        this.level = level;
        this.zone = zone;

        this.date.setTimeZone(this.zone);
        fatal = shouldLog(LogLevel.FATAL);
        error = shouldLog(LogLevel.ERROR);
        warn = shouldLog(LogLevel.WARN);
        info = shouldLog(LogLevel.INFO);
        debug = shouldLog(LogLevel.DEBUG);
        trace = shouldLog(LogLevel.TRACE);
    }

    /**
     * Данный метод возвращает уровнь логгирования.
     * <p>
     * This method returns the logging level.
     */
    public LogLevel getLogLevel() {
        return level;
    }

    /**
     * Данный метод возвращает часовой пояс данного логгера.
     * <p>
     * This method returns the time zone of the given logger.
     */
    public TimeZone getTimeZone() {
        return zone;
    }


    /**
     * Данный метод записывает сообщение во все Appender'ы.
     * Если уровень логгирования данного метода выше или равен уровню логгирования LoggerGroup.
     * <p>
     * This method writes a message to all Appenders.
     * If the logging level of this method is higher or equal to the logging level of LoggerGroup.
     * <p>
     * <p>
     * Не должно быть выбрашено каких-либо исключений.
     * <p>
     * No exceptions should be thrown.
     * <p>
     *
     * @param message Строка которую необходимо записать. The string to be logged.
     */
    public void trace(String message) {
        if (!trace) return;
        try {
            for (Appender appender : appenders) {
                try {
                    appender.append(format(LogLevel.TRACE.toString(), message, null, false));
                } catch (Exception ignore) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Данный метод записывает сообщение во все Appender'ы.
     * Если уровень логгирования данного метода выше или равен уровню логгирования LoggerGroup.
     * <p>
     * This method writes a message to all Appenders.
     * If the logging level of this method is higher or equal to the logging level of LoggerGroup.
     * <p>
     * <p>
     * Не должно быть выбрашено каких-либо исключений.
     * <p>
     * No exceptions should be thrown.
     * <p>
     *
     * @param message Строка которую необходимо записать. The string to be logged.
     * @param obj     Любой объект который, также необходимо записать. Any object that also needs to be recorded.
     */
    public void trace(String message, Object obj) {
        if (!trace) return;
        try {
            for (Appender appender : appenders) {
                try {
                    appender.append(format(LogLevel.TRACE.toString(), message, obj, true));
                } catch (Exception ignore) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Данный метод записывает сообщение во все Appender'ы.
     * Если уровень логгирования данного метода выше или равен уровню логгирования LoggerGroup.
     * <p>
     * This method writes a message to all Appenders.
     * If the logging level of this method is higher or equal to the logging level of LoggerGroup.
     * <p>
     * <p>
     * Не должно быть выбрашено каких-либо исключений.
     * <p>
     * No exceptions should be thrown.
     * <p>
     *
     * @param message Строка которую необходимо записать. The string to be logged.
     */
    public void debug(String message) {
        if (!debug) return;
        try {
            for (Appender appender : appenders) {
                try {
                    appender.append(format(LogLevel.DEBUG.toString(), message, null, false));
                } catch (Exception ignore) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Данный метод записывает сообщение во все Appender'ы.
     * Если уровень логгирования данного метода выше или равен уровню логгирования LoggerGroup.
     * <p>
     * This method writes a message to all Appenders.
     * If the logging level of this method is higher or equal to the logging level of LoggerGroup.
     * <p>
     * <p>
     * Не должно быть выбрашено каких-либо исключений.
     * <p>
     * No exceptions should be thrown.
     * <p>
     *
     * @param message Строка которую необходимо записать. The string to be logged.
     * @param obj     Любой объект который, также необходимо записать. Any object that also needs to be recorded.
     */
    public void debug(String message, Object obj) {
        if (!debug) return;
        try {
            for (Appender appender : appenders) {
                try {
                    appender.append(format(LogLevel.DEBUG.toString(), message, obj, true));
                } catch (Exception ignore) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Данный метод записывает сообщение во все Appender'ы.
     * Если уровень логгирования данного метода выше или равен уровню логгирования LoggerGroup.
     * <p>
     * This method writes a message to all Appenders.
     * If the logging level of this method is higher or equal to the logging level of LoggerGroup.
     * <p>
     * <p>
     * Не должно быть выбрашено каких-либо исключений.
     * <p>
     * No exceptions should be thrown.
     * <p>
     *
     * @param message Строка которую необходимо записать. The string to be logged.
     */
    public void info(String message) {
        if (!info) return;
        try {
            for (Appender appender : appenders) {
                try {
                    appender.append(format(LogLevel.INFO.toString(), message, null, false));
                } catch (Exception ignore) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Данный метод записывает сообщение во все Appender'ы.
     * Если уровень логгирования данного метода выше или равен уровню логгирования LoggerGroup.
     * <p>
     * This method writes a message to all Appenders.
     * If the logging level of this method is higher or equal to the logging level of LoggerGroup.
     * <p>
     * <p>
     * Не должно быть выбрашено каких-либо исключений.
     * <p>
     * No exceptions should be thrown.
     * <p>
     *
     * @param message Строка которую необходимо записать. The string to be logged.
     * @param obj     Любой объект который, также необходимо записать. Any object that also needs to be recorded.
     */
    public void info(String message, Object obj) {
        if (!info) return;
        try {
            for (Appender appender : appenders) {
                try {
                    appender.append(format(LogLevel.INFO.toString(), message, obj, true));
                } catch (Exception ignore) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Данный метод записывает сообщение во все Appender'ы.
     * Если уровень логгирования данного метода выше или равен уровню логгирования LoggerGroup.
     * <p>
     * This method writes a message to all Appenders.
     * If the logging level of this method is higher or equal to the logging level of LoggerGroup.
     * <p>
     * <p>
     * Не должно быть выбрашено каких-либо исключений.
     * <p>
     * No exceptions should be thrown.
     * <p>
     *
     * @param message Строка которую необходимо записать. The string to be logged.
     */
    public void warn(String message) {
        if (!warn) return;
        try {
            for (Appender appender : appenders) {
                try {
                    appender.append(format(LogLevel.WARN.toString(), message, null, false));
                } catch (Exception ignore) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Данный метод записывает сообщение во все Appender'ы.
     * Если уровень логгирования данного метода выше или равен уровню логгирования LoggerGroup.
     * <p>
     * This method writes a message to all Appenders.
     * If the logging level of this method is higher or equal to the logging level of LoggerGroup.
     * <p>
     * <p>
     * Не должно быть выбрашено каких-либо исключений.
     * <p>
     * No exceptions should be thrown.
     * <p>
     *
     * @param message Строка которую необходимо записать. The string to be logged.
     * @param obj     Любой объект который, также необходимо записать. Any object that also needs to be recorded.
     */
    public void warn(String message, Object obj) {
        if (!warn) return;
        try {
            for (Appender appender : appenders) {
                try {
                    appender.append(format(LogLevel.WARN.toString(), message, obj, true));
                } catch (Exception ignore) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Данный метод записывает сообщение во все Appender'ы.
     * Если уровень логгирования данного метода выше или равен уровню логгирования LoggerGroup.
     * <p>
     * This method writes a message to all Appenders.
     * If the logging level of this method is higher or equal to the logging level of LoggerGroup.
     * <p>
     * <p>
     * Не должно быть выбрашено каких-либо исключений.
     * <p>
     * No exceptions should be thrown.
     * <p>
     *
     * @param message Строка которую необходимо записать. The string to be logged.
     */
    public void error(String message) {
        if (!error) return;
        try {
            for (Appender appender : appenders) {
                try {
                    appender.append(format(LogLevel.ERROR.toString(), message, null, false));
                } catch (Exception ignore) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Данный метод записывает сообщение во все Appender'ы.
     * Если уровень логгирования данного метода выше или равен уровню логгирования LoggerGroup.
     * <p>
     * This method writes a message to all Appenders.
     * If the logging level of this method is higher or equal to the logging level of LoggerGroup.
     * <p>
     * <p>
     * Не должно быть выбрашено каких-либо исключений.
     * <p>
     * No exceptions should be thrown.
     * <p>
     *
     * @param message Строка которую необходимо записать. The string to be logged.
     * @param obj     Любой объект который, также необходимо записать. Any object that also needs to be recorded.
     */
    public void error(String message, Object obj) {
        if (!error) return;
        try {
            for (Appender appender : appenders) {
                try {
                    appender.append(format(LogLevel.ERROR.toString(), message, obj, true));
                } catch (Exception ignore) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Данный метод записывает сообщение во все Appender'ы.
     * Если уровень логгирования данного метода выше или равен уровню логгирования LoggerGroup.
     * <p>
     * This method writes a message to all Appenders.
     * If the logging level of this method is higher or equal to the logging level of LoggerGroup.
     * <p>
     * <p>
     * Не должно быть выбрашено каких-либо исключений.
     * <p>
     * No exceptions should be thrown.
     * <p>
     *
     * @param message Строка которую необходимо записать. The string to be logged.
     */
    public void fatal(String message) {
        if (!fatal) return;
        try {
            for (Appender appender : appenders) {
                try {
                    appender.append(format(LogLevel.FATAL.toString(), message, null, false));
                } catch (Exception ignore) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Данный метод записывает сообщение во все Appender'ы.
     * Если уровень логгирования данного метода выше или равен уровню логгирования LoggerGroup.
     * <p>
     * This method writes a message to all Appenders.
     * If the logging level of this method is higher or equal to the logging level of LoggerGroup.
     * <p>
     * <p>
     * Не должно быть выбрашено каких-либо исключений.
     * <p>
     * No exceptions should be thrown.
     * <p>
     *
     * @param message Строка которую необходимо записать. The string to be logged.
     * @param obj     Любой объект который, также необходимо записать. Any object that also needs to be recorded.
     */
    public void fatal(String message, Object obj) {
        if (!fatal) return;
        try {
            for (Appender appender : appenders) {
                try {
                    appender.append(format(LogLevel.FATAL.toString(), message, obj, true));
                } catch (Exception ignore) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Вызывается 1 раз при создании класса, для того, чтобы установить boolean нужно ли логгировать с данным уровнем.
    //Важен порядок элементов в enum LogLevel.
    private boolean shouldLog(LogLevel currentLevel) {
        if (this.level == LogLevel.ALL) return true;
        if (this.level == LogLevel.OFF) return false;
        return currentLevel.ordinal() >= this.level.ordinal();
    }

    //Добавляет форматирование строки, оно неизменно.
    private String format(String type, String message, Object obj, boolean formatObject) {
        Date now = new Date();
        String[] clazzMethodLine = LoggerFactory.getClassMethodLine(3);
        StringBuilder builder = new StringBuilder()
                .append(date.format(now))
                .append('[').append(type)
                .append("][")
                .append(Thread.currentThread().getName())
                .append("][").append(clazzMethodLine[0])
                .append("][").append(clazzMethodLine[1])
                .append(':').append(clazzMethodLine[2])
                .append("] ").append(message)
                .append('\n');
        if (formatObject) builder.append(objToString(obj));

        return builder.toString();
    }

    //Превращает объект в строку, если это Throwable то расписывает весь StackTrace.
    private static String objToString(Object obj) {
        if (obj == null) return "null\n";
        if (obj instanceof Throwable throwable) {
            StringBuilder builder = new StringBuilder();
            builder.append(throwable.getClass().getName());
            if (throwable.getLocalizedMessage() != null) builder.append(": ").append(throwable.getLocalizedMessage());
            builder.append('\n');
            for (StackTraceElement element : throwable.getStackTrace()) {
                builder.append("\tat ").append(element).append('\n');
            }
            return builder.toString();
        }
        return obj + "\n";
    }


}
