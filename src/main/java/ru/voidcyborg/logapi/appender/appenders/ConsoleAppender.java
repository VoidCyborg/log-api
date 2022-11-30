package ru.voidcyborg.logapi.appender.appenders;

import ru.voidcyborg.logapi.appender.Appender;

public class ConsoleAppender implements Appender {

    @Override
    public boolean append(String text) {
        System.out.print(text);
        return true;
    }
}
