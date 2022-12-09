package ru.voidcyborg.logapi.appender.appenders;

import ru.voidcyborg.logapi.appender.Appender;

import java.util.HashMap;

public class ConsoleAppender implements Appender {

    @Override
    public void parseSettings(HashMap<String, String> settings) {

    }

    @Override
    public boolean append(String text) {
        System.out.print(text);
        return true;
    }
}
