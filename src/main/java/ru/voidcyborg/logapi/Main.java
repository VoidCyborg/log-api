package ru.voidcyborg.logapi;

import ru.voidcyborg.logapi.appender.appenders.ConsoleAppender;
import ru.voidcyborg.logapi.logger.Logger;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        Logger logger = new Logger("Bebra", new ConsoleAppender());
        logger.info("bebra");
        logger.debug("debugTest");
        Exception e = new NullPointerException("BEBra reason");
        logger.error("ERRROROROROOR", e);
        methodLOL(logger);
        HashMap<String, Integer> map = new HashMap<>();
        map.put("KEy", 5);
        map.put("lol", 2);
        logger.fatal("Fatal object", map);
    }

    public static void methodLOL(Logger logger){
        logger.info("Bebroken");
    }
}