package ru.voidcyborg.logapi;

import ru.voidcyborg.logapi.appender.appenders.ConsoleAppender;
import ru.voidcyborg.logapi.logger.Logger;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        Logger logger = new Logger("Bebra", new ConsoleAppender());
        logger.info("bebra");
    }
}