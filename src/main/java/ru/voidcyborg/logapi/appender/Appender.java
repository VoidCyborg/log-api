package ru.voidcyborg.logapi.appender;

import java.util.HashMap;

public interface Appender {
    void parseSettings(HashMap<String, String> settings);

    boolean append(String text);
}
