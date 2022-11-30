package ru.voidcyborg.logapi.logger;

import ru.voidcyborg.logapi.appender.Appender;
import ru.voidcyborg.logapi.level.LogLevel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;

public final class Logger {

    private static final SimpleDateFormat date = new SimpleDateFormat("[dd-MM-yyyy]");
    private static final SimpleDateFormat time = new SimpleDateFormat("[HH:mm:ss]");

    static {
        date.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        time.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
    }


    private final String name;
    private final Appender appender;
    private final LogLevel level = LoggerFactory.getLogLevel();


    private final boolean fatal = shouldLog(LogLevel.FATAL);
    private final boolean error = shouldLog(LogLevel.ERROR);
    private final boolean warn = shouldLog(LogLevel.WARN);
    private final boolean info = shouldLog(LogLevel.INFO);
    private final boolean debug = shouldLog(LogLevel.DEBUG);
    private final boolean trace = shouldLog(LogLevel.TRACE);


    public Logger(String name, Appender appender) {//TODO remove public
        this.name = name;
        this.appender = appender;
    }


    private boolean shouldLog(LogLevel currentLevel) {
        if (this.level == LogLevel.ALL) return true;
        if (this.level == LogLevel.OFF) return false;
        return currentLevel.ordinal() >= this.level.ordinal();
    }


    public void trace(String message) {
        if (!trace) return;

    }

    public void trace(String message, Object obj) {
        if (!trace) return;

    }

    public void debug(String message) {
        if (!debug) return;

    }

    public void debug(String message, Object obj) {
        if (!debug) return;

    }

    public void info(String message) {
        if (!info) return;
        try {
            this.appender.append(format(LogLevel.INFO.toString(), message, null));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void info(String message, Object obj) {
        if (!info) return;

    }

    public void warn(String message) {
        if (!warn) return;

    }

    public void warn(String message, Object obj) {
        if (!warn) return;

    }

    public void error(String message) {
        if (!error) return;

    }

    public void error(String message, Object obj) {
        if (!error) return;

    }

    public void fatal(String message) {
        if (!fatal) return;

    }

    public void fatal(String message, Object obj) {
        if (!fatal) return;

    }

    private String format(String type, String message, Throwable throwable) {
        Date now = new Date();
        String[] clazzAndName = getClassAndName(3);

        return date.format(now) + time.format(now) + "[" + type + "][" + Thread.currentThread().getName() + "][" + clazzAndName[0] + "][" + clazzAndName[1] + "] " + message;
    }

   /* private String format(String type, String message, Object obj) {

    }*/
    //TODO

    private static String[] getClassAndName(int skip) {
        StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

        Optional<StackWalker.StackFrame> stackFrame = walker.walk(frames -> frames
                .skip(skip)
                .findFirst());

        return stackFrame.map(frame -> new String[]{frame.getDeclaringClass().getSimpleName(), frame.getMethodName()}).
                orElse(new String[]{"#unknown", "#unknown"});
    }


}
