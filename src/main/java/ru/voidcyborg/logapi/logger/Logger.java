package ru.voidcyborg.logapi.logger;

import ru.voidcyborg.logapi.appender.Appender;
import ru.voidcyborg.logapi.level.LogLevel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;

public final class Logger {

    private static final SimpleDateFormat date = new SimpleDateFormat("[dd-MM-yyyy]");
    private static final SimpleDateFormat time = new SimpleDateFormat("[HH:mm:ss]");

    static {
        date.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        time.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
    }

    private final Set<Appender> appenders;
    private final LogLevel level;

    private final boolean fatal;
    private final boolean error;
    private final boolean warn;
    private final boolean info;
    private final boolean debug;
    private final boolean trace;

    Logger(Set<Appender> appenders, LogLevel level) {
        this.appenders = appenders;
        this.level = level;

        fatal = shouldLog(LogLevel.FATAL);
        error = shouldLog(LogLevel.ERROR);
        warn = shouldLog(LogLevel.WARN);
        info = shouldLog(LogLevel.INFO);
        debug = shouldLog(LogLevel.DEBUG);
        trace = shouldLog(LogLevel.TRACE);
    }


    private boolean shouldLog(LogLevel currentLevel) {
        if (this.level == LogLevel.ALL) return true;
        if (this.level == LogLevel.OFF) return false;
        return currentLevel.ordinal() >= this.level.ordinal();
    }


    public void trace(String message) {
        if (!trace) return;
        try {
            for (Appender appender : appenders) {
                try {
                    appender.append(format(LogLevel.TRACE.toString(), message, null));
                } catch (Exception ignore) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void trace(String message, Object obj) {
        if (!trace) return;
        try {
            for (Appender appender : appenders) {
                try {
                    appender.append(format(LogLevel.TRACE.toString(), message, obj));
                } catch (Exception ignore) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void debug(String message) {
        if (!debug) return;
        try {
            for (Appender appender : appenders) {
                try {
                    appender.append(format(LogLevel.DEBUG.toString(), message, null));
                } catch (Exception ignore) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void debug(String message, Object obj) {
        if (!debug) return;
        try {
            for (Appender appender : appenders) {
                try {
                    appender.append(format(LogLevel.DEBUG.toString(), message, obj));
                } catch (Exception ignore) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void info(String message) {
        if (!info) return;
        try {
            for (Appender appender : appenders) {
                try {
                    appender.append(format(LogLevel.INFO.toString(), message, null));
                } catch (Exception ignore) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void info(String message, Object obj) {
        if (!info) return;
        try {
            for (Appender appender : appenders) {
                try {
                    appender.append(format(LogLevel.INFO.toString(), message, obj));
                } catch (Exception ignore) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void warn(String message) {
        if (!warn) return;
        try {
            for (Appender appender : appenders) {
                try {
                    appender.append(format(LogLevel.WARN.toString(), message, null));
                } catch (Exception ignore) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void warn(String message, Object obj) {
        if (!warn) return;
        try {
            for (Appender appender : appenders) {
                try {
                    appender.append(format(LogLevel.WARN.toString(), message, obj));
                } catch (Exception ignore) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void error(String message) {
        if (!error) return;
        try {
            for (Appender appender : appenders) {
                try {
                    appender.append(format(LogLevel.ERROR.toString(), message, null));
                } catch (Exception ignore) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void error(String message, Object obj) {
        if (!error) return;
        try {
            for (Appender appender : appenders) {
                try {
                    appender.append(format(LogLevel.ERROR.toString(), message, obj));
                } catch (Exception ignore) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fatal(String message) {
        if (!fatal) return;
        try {
            for (Appender appender : appenders) {
                try {
                    appender.append(format(LogLevel.FATAL.toString(), message, null));
                } catch (Exception ignore) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fatal(String message, Object obj) {
        if (!fatal) return;
        try {
            for (Appender appender : appenders) {
                try {
                    appender.append(format(LogLevel.FATAL.toString(), message, obj));
                } catch (Exception ignore) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String format(String type, String message, Object obj) {
        Date now = new Date();
        String[] clazzAndName = LoggerFactory.getClassMethodLine(3);
        StringBuilder builder = new StringBuilder()
                .append(date.format(now))
                .append(time.format(now))
                .append('[').append(type)
                .append("][")
                .append(Thread.currentThread().getName())
                .append("][").append(clazzAndName[0])
                .append("][").append(clazzAndName[1])
                .append(':').append(clazzAndName[2])
                .append("] ").append(message)
                .append('\n');
        if (obj != null) builder.append(objToString(obj));

        return builder.toString();
    }

    private static String objToString(Object obj) {
        if (obj == null) return "null\n";
        if (obj instanceof Throwable throwable) {
            StringBuilder builder = new StringBuilder();
            builder.append(throwable.getClass().getName());
            if (throwable.getLocalizedMessage() != null) builder.append(": ").append(throwable.getLocalizedMessage());
            builder.append('\n');
            for (StackTraceElement element : throwable.getStackTrace()) {
                builder.append("\tat ").append(element).append('\n');
            }
            return builder.toString();
        }
        return obj + "\n";
    }


}
