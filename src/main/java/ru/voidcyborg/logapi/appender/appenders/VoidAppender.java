package ru.voidcyborg.logapi.appender.appenders;

import ru.voidcyborg.logapi.appender.Appender;

import java.util.Map;

public class VoidAppender implements Appender {

    @Override
    public void parseSettings(Map<String, String> settings) {
    }

    @Override
    public boolean append(String text) {
        return text != null && !text.isEmpty();
    }
}
