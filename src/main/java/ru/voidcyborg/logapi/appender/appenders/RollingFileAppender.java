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

public class RollingFileAppender implements Appender {


    private static final String PID = ProcessHandle.current().pid() + "-";
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private volatile boolean settingsParsed;
    private volatile String name = "log";
    private volatile String type = "";
    private volatile Path path = Path.of("");

    private volatile int maxSize = 1024_000;
    private volatile int maxFiles = -1;

    private volatile int index = 0;


    @Override
    public synchronized void parseSettings(HashMap<String, String> settings) {
        if (settings == null) return;
        if (settingsParsed) return;

        String[] nameType = separateNameAndType(settings.get("fileName"));
        name = nameType[0];
        type = nameType[1];
        maxSize = parseSize(settings.get("maxFileSize"));
        maxFiles = parseMaxFiles(settings.get("maxFiles"));
        path = parsePath(settings.get("folderPath"));
        settingsParsed = true;
    }

    @Override
    public synchronized boolean append(String text) {
        if (text == null) return false;
        if (text.isEmpty()) return false;

        executor.execute(() -> {
            ByteBuffer buffer = ByteBuffer.wrap(text.getBytes(StandardCharsets.UTF_8));

            String fileName;
            while (true) {
                fileName = generateName();

                try (FileChannel channel = FileChannel.open(this.path.resolve(fileName), StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                    if (channel.size() + buffer.remaining() > maxSize) {
                        if (maxFiles > 0 && index + 1 >= maxFiles) index = 0;
                        else index++;


                        fileName = generateName();

                        if (Files.exists(this.path.resolve(fileName))) Files.delete(this.path.resolve(fileName));

                        continue;
                    }

                    while (buffer.hasRemaining()) {
                        if (channel.write(buffer) <= 0) break;
                    }

                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }

        });

        return true;
    }

    private String generateName() {
        return PID + name + "-" + index + type;
    }

    private Path parsePath(String s) {
        try {
            Path path = Path.of(s);

            Files.createDirectories(path);
            if (!Files.isDirectory(path)) throw new NullPointerException();

            return path;
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
