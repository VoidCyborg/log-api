package ru.voidcyborg.logapi.level;

public enum LogLevel {
    OFF,
    FATAL,
    ERROR,
    WARN,
    INFO,
    DEBUG,
    TRACE,
    ALL;

    private final String toString = calcString();


    private String calcString() {
        StringBuilder result = new StringBuilder(this.name());
        if (result.length() == 5) return result.toString();
        for (int i = 0; i < 5 - result.length(); i++) {
            result.append(' ');
        }
        return result.toString();
    }

    @Override
    public String toString() {
        return toString;
    }
}
