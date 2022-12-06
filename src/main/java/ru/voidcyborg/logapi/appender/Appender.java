package ru.voidcyborg.logapi.appender;

public interface Appender {
    void parseSettings(String params);

    boolean append(String text);
}
