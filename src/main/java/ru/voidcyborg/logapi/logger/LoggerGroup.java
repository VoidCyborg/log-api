package ru.voidcyborg.logapi.logger;

import ru.voidcyborg.logapi.appender.Appender;
import ru.voidcyborg.logapi.level.LogLevel;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Класс {@code LoggerGroup} предствляет из себя объект, который объединяет несколько Logger'ов и позволяет добавлять Appender'ы.
 * <p>
 * <p>
 * The {@code LoggerGroup} class is an object that combines several Loggers and allows you to add Appenders.
 * <p>
 * <p>
 * Из данного класса можно получить Logger для конкретного класса. {@code group.getLogger();}.
 * Пример использования:
 * <p>
 * <p>
 * From this class, you can get a Logger for a particular class.
 * <p>
 * {@code group.getLogger();}.
 * <p>
 * Usage example:
 * <blockquote><pre>
 *     public void methodA(){
 *        String str = "abc";
 *        Logger logger = LoggerFactory.getLoggerGroup(String name).getLogger();
 *        logger.info(str);
 *        logger.fatal("Error because of ", str);
 *     }
 * </pre></blockquote><p>
 * Экземпляры данного класса может создать только {@code LoggerFactory}.
 * Не рекомендуется передавать объект {@code LoggerGroup} между другими объектами и классами.
 * <p>
 * Instances of this class can only be created by {@code LoggerFactory}.
 * It is not recommended to pass the {@code LoggerGroup} object between other objects and classes.
 * <p>
 *
 * @author VoidCyborg
 * @see ru.voidcyborg.logapi.level.LogLevel
 * @see ru.voidcyborg.logapi.appender.Appender
 * @see ru.voidcyborg.logapi.logger.LoggerFactory
 * @see java.text.SimpleDateFormat
 * @see java.util.Map
 * @see java.lang.String
 */
public final class LoggerGroup {

    private final LogLevel level;
    private final TimeZone zone;
    private final Set<Appender> appenders = ConcurrentHashMap.newKeySet();
    private final Map<Class<?>, Logger> loggers = new ConcurrentHashMap<>();
    private final Logger defaultLogger;


    //Передаваемый уровень и зона не должны быть null.
    LoggerGroup(LogLevel level, TimeZone zone) {
        this.zone = zone;
        this.level = level;
        this.defaultLogger = loggers.computeIfAbsent(LoggerGroup.class, clazz -> new Logger(this.appenders, this.level, this.zone));
    }


    /**
     * Данный метод позволяет получить логгер для класса в котором был вызван.
     * <p>
     * This method allows you to get a logger for the class in which it was called.
     * <p>
     * <p>
     * Не должно быть выбрашено каких-либо исключений.
     * В случае, если не удалось определить класс который вызвал метод, будет возвращён дефолтный логгер класса {@code LoggerGroup}.
     * <p>
     * No exceptions should be thrown.
     * In case it was not possible to determine the class that called the method, the default class logger {@code LoggerGroup} will be returned.
     * <p>
     *
     * @return Логгер для вызывающего класса. Logger for the calling class.
     */
    public Logger getLogger() {
        Class<?> frame = LoggerFactory.getClass(2);

        if (frame == null) {
            this.defaultLogger.error("Failed to create logger by stack");
            return this.defaultLogger;
        }

        return loggers.computeIfAbsent(frame, clazz -> {
            defaultLogger.trace("Created new logger - " + clazz.getSimpleName() + " - " + this.level + " - " + this.zone);
            return new Logger(this.appenders, this.level, this.zone);
        });
    }


    /**
     * Данный метод уничтожает группу логгирования.
     * <p>
     * This method destroy the logging group.
     * <p>
     * <p>
     * Не должно быть выбрашено каких-либо исключений.
     * <p>
     * No exceptions should be thrown.
     * <p>
     */
    public void destroy() {
        try {
            List<Appender> cashed = new ArrayList<>(this.appenders);
            this.appenders.clear();

            for (Appender appender : cashed) {
                appender.destroy();
            }
        } catch (Exception ignore) {
        }
    }

    /**
     * Данный метод добавляет новый Appender в данную группу логгирования.
     * <p>
     * This method adds a new Appender to the given logging group.
     * <p>
     * <p>
     * Не должно быть выбрашено каких-либо исключений.
     * <p>
     * No exceptions should be thrown.
     * <p>
     *
     * @param appender Appender который необходимо добавить. Appender to be added.
     * @return Данную группу логгирования. This logging group.
     */
    public LoggerGroup addAppender(Appender appender) {
        if (appender != null) {
            appenders.add(appender);
        }
        return this;
    }

    /**
     * Данный метод добавляет новые Appender'ы в данную группу логгирования.
     * <p>
     * This method adds new Appenders to the given logging group.
     * <p>
     * <p>
     * Не должно быть выбрашено каких-либо исключений.
     * <p>
     * No exceptions should be thrown.
     * <p>
     *
     * @param appenders Appender'ы которые необходимо добавить. Appenders to be added.
     * @return Данную группу логгирования. This logging group.
     */
    public LoggerGroup addAppenders(Appender... appenders) {
        if (appenders == null) return this;

        try {
            for (Appender appender : appenders) {
                if (appender != null) this.appenders.add(appender);
            }
        } catch (Exception e) {
            this.defaultLogger.error("Failed to add Appenders to LoggerGroup.");
        }

        return this;
    }

}
