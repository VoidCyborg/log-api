package ru.voidcyborg.logapi.appender.appenders;

import ru.voidcyborg.logapi.appender.Appender;

import java.util.HashMap;

public class FileAppender implements Appender {//TODO
    @Override
    public void parseSettings(HashMap<String, String> settings) {

    }

    @Override
    public boolean append(String text){
        return true;//TODO
    }
}
