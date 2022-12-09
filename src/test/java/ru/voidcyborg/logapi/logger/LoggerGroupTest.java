package ru.voidcyborg.logapi.logger;

import org.junit.jupiter.api.Test;
import ru.voidcyborg.logapi.appender.appenders.ConsoleAppender;
import ru.voidcyborg.logapi.level.LogLevel;

class LoggerGroupTest {

    @Test
    void getLogger() {

        for (LogLevel level : LogLevel.values()) {
            System.out.println("LEVEL:" + level);
            Logger logger = LoggerFactory.createCustomLoggerGroup(level).addAppender(new ConsoleAppender()).getLogger();

            logger.trace("trace");
            logger.debug("debug");
            logger.info("info");
            logger.warn("warn");
            logger.error("error");
            logger.fatal("fatal");
        }
    }


}