package ru.voidcyborg.logapi.appender;

import java.util.Map;

public interface Appender {
    void parseSettings(Map<String, String> settings);

    boolean append(String text);
}
