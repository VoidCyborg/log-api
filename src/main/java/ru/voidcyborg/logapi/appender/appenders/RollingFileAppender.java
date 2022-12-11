package ru.voidcyborg.logapi.appender.appenders;

import ru.voidcyborg.logapi.appender.Appender;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RollingFileAppender implements Appender {//TODO


    private static final String PID = ProcessHandle.current().pid() + "-";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private volatile String name = "log";
    private volatile String type = "";
    private volatile Path path = Path.of("");

    private volatile int maxSize = 1024_000;
    private volatile int maxFiles = -1;

    private volatile int index = 0;


    @Override
    public synchronized void parseSettings(HashMap<String, String> settings) {
        if (settings == null) return;

        String[] nameType = separateNameAndType(settings.get("fileName"));
        name = nameType[0];
        type = nameType[1];
        maxSize = parseSize(settings.get("maxFileSize"));
        maxFiles = parseMaxFiles(settings.get("maxFiles"));

    }

    @Override
    public synchronized boolean append(String text) {
        if (text == null) return false;


        while (true) {
            try (FileChannel channel = FileChannel.open(this.path.resolve(name + "-" + index + type), StandardOpenOption.CREATE_NEW, StandardOpenOption.APPEND)) {
                ByteBuffer buffer = ByteBuffer.wrap(text.getBytes(StandardCharsets.UTF_8));
                if (channel.size() + buffer.remaining() > maxSize) {
                    index++;
                }
            } catch (Exception ignore) {
                break;
            }
            if (maxFiles>0){
                if(index>=maxFiles) {
                    Files.delete();
                }
            }
        }


        return true;//TODO
    }

    private Path parsePath(String s) {
        try {
            return Path.of(s);
        } catch (Exception ignore) {
        }
        return Path.of("");
    }

    private int parseMaxFiles(String s) {
        int files = -1;

        try {
            files = Integer.parseInt(s);
        } catch (Exception ignore) {
        }

        return files;
    }

    private int parseSize(String s) {
        int size = 1024_000;

        try {
            size = Integer.parseInt(s);
        } catch (Exception ignore) {
        }
        if (size < 1024) size = 1024;

        return size;
    }

    private String[] separateNameAndType(String fileName) {
        if (fileName == null || fileName.isBlank()) return new String[]{"log", ""};

        String[] result = new String[2];
        StringBuilder builder = new StringBuilder();

        char c;
        for (int i = fileName.length() - 1; i >= 0; i--) {
            c = fileName.charAt(i);
            builder.append(c);
            if (c == '.') {
                i--;
                result[1] = builder.reverse().toString();
                builder.setLength(0);

                for (; i >= 0; i--) {
                    builder.append(fileName.charAt(i));
                }
            }
        }

        result[0] = builder.reverse().toString().trim();
        if (result[0].isBlank()) result[0] = "log";


        if (result[1] == null) result[1] = "";
        result[1] = result[1].trim();

        if (result[1].length() <= 1) result[1] = "";

        return result;
    }
}
