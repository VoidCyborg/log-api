package ru.voidcyborg.logapi.settings;

import ru.voidcyborg.logapi.appender.Appender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class Settings {

    private final String[] args;

    private final Appender[] appenders;
    private final HashMap<String, String> appenderSettings = new HashMap<>();

    public Settings(String[] args) throws SettingsInitException {
        if (args == null) throw new SettingsInitException("Parameters can't be null");
        this.args = args.clone();
        this.appenders = createAppenders();
        this.parseArguments();
    }


    private Appender[] createAppenders() throws SettingsInitException {
        List<String> paths = new ArrayList<>();
        for (String s : this.args) {
            if (s == null) continue;
            if (!s.startsWith("appender=")) continue;
            String[] parts = s.split("=");
            if (parts.length != 2) throw new SettingsInitException("Wrong settings syntax path:" + s);

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

    private void parseArguments() throws SettingsInitException {

    }
}
