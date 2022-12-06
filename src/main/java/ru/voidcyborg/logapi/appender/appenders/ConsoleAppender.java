package ru.voidcyborg.logapi.appender.appenders;

import ru.voidcyborg.logapi.appender.Appender;

import java.util.zip.DataFormatException;

public class ConsoleAppender implements Appender {

    @Override
    public void parseSettings(String params){
    }

    @Override
    public boolean append(String text) {
        System.out.print(text);
        return true;
    }
}
