package ru.voidcyborg.logapi.settings;

import ru.voidcyborg.logapi.appender.Appender;
import ru.voidcyborg.logapi.level.LogLevel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public final class Settings {

    private final String[] args;
    private final LogLevel level;
    private final Appender[] appenders;
    private final HashMap<String, String> appenderSettings = new HashMap<>();

    public Settings(String[] args) throws SettingsInitException {
        if (args == null) throw new SettingsInitException("Parameters can't be null");
        this.args = args.clone();
        this.level = parseLevel();
        this.appenders = createAppenders();
        this.parseAppenderSettings();

        try {
            for (Appender appender : appenders) {
                appender.parseSettings(this.getAppenderSettings());
            }
        } catch (Exception e) {
            throw new SettingsInitException("Failed to init settings in Appenders.");
        }
    }

    @SuppressWarnings("unchecked")
    public HashMap<String, String> getAppenderSettings() {
        return (HashMap<String, String>) appenderSettings.clone();
    }

    public Appender[] getAppenders() {
        return Arrays.copyOf(appenders, appenders.length);
    }

    public LogLevel getLevel() {
        return level;
    }


    private LogLevel parseLevel() throws SettingsInitException {
        LogLevel level;
        for (String line : args) {
            try {
                if (!line.startsWith("LogLevel=")) continue;
                line = line.replace("LogLevel=", "");
                level = LogLevel.valueOf(line);

                return level;
            } catch (Exception ignore) {
            }
        }

        throw new SettingsInitException("Failed to find LogLevel in settings");
    }

    private Appender[] createAppenders() throws SettingsInitException {
        List<String> paths = new ArrayList<>();
        for (String s : this.args) {
            if (s == null) continue;
            if (!s.startsWith("appender=")) continue;
            String[] parts = s.split("=");
            if (parts.length != 2) throw new SettingsInitException("Wrong settings syntax:" + s);

            paths.add(parts[1]);
        }

        if (paths.isEmpty()) throw new SettingsInitException("No single path to the Appender class.");

        List<Appender> appenders = new ArrayList<>();
        for (String s : paths) {
            try {
                Appender appender = (Appender) Class.forName(s).getDeclaredConstructor().newInstance();
                appenders.add(appender);
            } catch (Exception e) {
                throw new SettingsInitException("Failed to create an Appender:" + s);
            }
        }

        return appenders.toArray(new Appender[0]);
    }

    private void parseAppenderSettings() throws SettingsInitException {
        String[] parts;
        String prefix = "appender.";
        try {
            for (String s : args) {
                parts = s.split("=");
                if (parts.length != 2) throw new SettingsInitException("Wrong settings syntax:" + s);
                if (!(parts[0].startsWith(prefix))) continue;

                this.appenderSettings.put(parts[0].substring(prefix.length()), parts[1]);
            }
        } catch (Exception e) {
            throw new SettingsInitException("Wrong setting syntax:" + e);
        }
    }
}
