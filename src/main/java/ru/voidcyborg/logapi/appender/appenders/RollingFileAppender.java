package ru.voidcyborg.logapi.appender.appenders;

import ru.voidcyborg.logapi.appender.Appender;
import ru.voidcyborg.logapi.settings.SettingsInitException;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Класс {@code RollingFileAppender} предствляет из себя реализацию записи строк в ротирующиеся файлы.
 * Его цель записывать строки в файл.
 * <p>
 * <p>
 * The {@code RollingFileAppender} class is an implementation of writing strings to rotating files.
 * It's purpose is to write lines to a file.
 * <p>
 * <p>
 * Для записи необходимо вызвать метод {@code append(String s)}.
 * Пример использования:
 * <p>
 * <p>
 * To write, you must call the {@code append(String s)} method.
 * Usage example:
 * <blockquote><pre>
 *     String str = "abc";
 *
 *     Appender appender = new RollingFileAppender();
 *     appender.parseSettings(settings);
 *
 *     appender.append(str);
 * </pre></blockquote><p>
 * <p>
 * Данный класс поддерживает несколько настроек:
 * <p><b>fileName</b> - строка с шаблоном имени(пример: log.txt). Должен состоять из имени и расширения. Расширение не обязательно.
 * A string with a name template (example: log.txt). Must consist of a name and an extension. The extension is not necessary.
 * <p><b>maxFileSize</b> - максимальный размер 1 файла в байтах. Минимально 1024 байта.
 * Maximum size of 1 file in bytes. Minimum 1024 bytes.
 * <p><b>maxFiles</b> - максимальное кол-во файлов для ротирования, если 0 или меньше то будет бессконечное кол-во файлов.
 * Maximum number of files to rotate, if 0 or less then there will be an infinite number of files.
 * <p><b>folderPath</b> - путь к директории в которой будут генерироваться файлы.
 * Path to the directory where files will be generated.
 *
 * @author VoidCyborg
 * @see ru.voidcyborg.logapi.appender.Appender
 * @see ru.voidcyborg.logapi.logger.Logger
 * @see java.util.Map
 * @see java.lang.String
 */
public final class RollingFileAppender implements Appender {


    private static final String PID;

    static {
        String temp;
        try {
            temp = ProcessHandle.current().pid() + "-";
        } catch (Exception e) {
            temp = (Math.random() * (Integer.MAX_VALUE / 50)) + "-";
        }
        PID = temp;
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private volatile FileChannel channel;
    private volatile FileLock lock;

    private volatile boolean settingsParsed;
    private volatile String name;
    private volatile String type;
    private volatile Path path;
    private volatile int maxSize = 1024_000;
    private volatile int maxFiles = -1;

    private volatile int index = 0;


    /**
     * Данный метод предназначен инициализации настроек для данного Appender'а.
     * Метод блокирующий, если настройки инициализированы и метод вызван повторно будет выбрашена ошибка.
     *
     * <p>
     * This method is intended to initialize the settings for this Appender.
     * The method is blocking, if the settings are initialized and the method is called again, an error will be thrown.
     * <p>
     *
     * @param settings Настройки в виде мапы. Map settings.
     * @throws ru.voidcyborg.logapi.settings.SettingsInitException
     */
    @Override
    public synchronized void parseSettings(Map<String, String> settings) throws SettingsInitException {
        if (settings == null) throw new SettingsInitException("Settings of RollingFileAppender can't be null.");
        if (settingsParsed)
            throw new SettingsInitException("Settings of RollingFileAppender can't be parsed second time.");

        try {
            String[] nameType = separateNameAndType(settings.get("fileName"));
            name = nameType[0];
            type = nameType[1];
            maxSize = parseSize(settings.get("maxFileSize"));
            maxFiles = parseMaxFiles(settings.get("maxFiles"));
            path = parsePath(settings.get("folderPath"));
        } catch (Exception e) {
            throw new SettingsInitException("Failed to parse settings of RollingFileAppender because of " + e);
        }
        settingsParsed = true;
    }

    /**
     * Данный метод предназначен для записи строки в файл.
     * Метод не блокирующий, то что он вернул true или false не значит что строка уже записана в файл.
     *
     * <p>
     * This method is for writing a string to files.
     * The method is not blocking, the fact that it returned true or false does not mean that the line has already been written to the file.
     * <p>
     * Не должно быть выбрашено каких-либо исключений.
     * <p>
     * No exceptions should be thrown.
     * <p>
     *
     * @param text Строка которую необходимо записать. The string to be appended.
     * @return Возвращает true если строка была передана в запись в файл. Returns true if the string was passed to write to a file.
     */
    @Override
    public synchronized boolean append(String text) {
        if (text == null) return false;
        if (text.isEmpty()) return false;
        if (!settingsParsed) return false;

        executor.execute(() -> {
            ByteBuffer buffer = ByteBuffer.wrap(text.getBytes(StandardCharsets.UTF_8));

            String fileName;
            while (true) {
                fileName = generateName();
                buffer.rewind();

                try {
                    if (channel == null) this.createChannel(this.path.resolve(fileName));


                    if (channel.size() + buffer.remaining() > maxSize || channel == null) {
                        if (maxFiles > 0 && index + 1 >= maxFiles) index = 0;
                        else index++;

                        fileName = generateName();

                        if (Files.exists(this.path.resolve(fileName))) Files.delete(this.path.resolve(fileName));

                        this.closeChannel();
                        continue;
                    }

                    if (!this.writeToChannel(buffer)) continue;

                    return;
                } catch (Exception e) {
                    this.closeChannel();
                    break;
                }
            }
        });

        return true;
    }

    /**
     * Останавливает работу executor
     */
    @Override
    public synchronized void destroy() {
        try {
            this.executor.shutdown();
        } catch (Exception ignore) {
        }
    }

    //Безопасно пишет в канал, если не удалось, возвращает false;
    private boolean writeToChannel(ByteBuffer buffer) {
        try {
            if (channel != null) {
                this.channel.position(this.channel.size());
                this.channel.write(buffer);
                return true;
            }
        } catch (Exception ignore) {
            this.closeChannel();
        }
        return false;
    }

    //Создаёт канал с замком, чтобы файл был доступен только данному классу.
    private void createChannel(Path path) {
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(this.path);
                Files.createFile(path);
            }
            RandomAccessFile temp = new RandomAccessFile(path.toString(), "rw");
            this.channel = temp.getChannel();
            this.lock = this.channel.lock();

            return;
        } catch (Exception e) {
            try {
                if (this.channel != null) channel.close();
            } catch (Exception ignore) {
            }
        }
        this.channel = null;
        this.lock = null;
    }

    //Безопасно закрывает канал и замок.
    private void closeChannel() {
        try {
            if (lock != null) lock.release();
        } catch (Exception ignore) {
        }
        try {
            if (channel != null) channel.close();
        } catch (Exception ignore) {
        }
        lock = null;
        channel = null;
    }

    //Генерирует имя файла основываять на ид программы, имени файла, индексе и типе.
    private String generateName() {
        return PID + name + "-" + index + type;
    }

    //Парсит путь к директории в которую будут сохраняться файлы логов.
    private Path parsePath(String s) throws IOException {
        Path path = Path.of(s);

        Files.createDirectories(path);
        if (!Files.isDirectory(path)) throw new NullPointerException();

        return path;
    }

    //Парсит максимальное кол-во файлов, если будет <= 0 то будет разрешено бесконечное кол-во файлов.
    private int parseMaxFiles(String s) {
        return Integer.parseInt(s);
    }

    //Парсит максимальный размер файла, если меньше 1024 то вернёт 1024 байта.
    private int parseSize(String s) {
        int size = Integer.parseInt(s);
        if (size < 1024) size = 1024;

        return size;
    }

    //Выковыривает какое имя будет у файла, и какое у него будет расширение. Пример: log.txt -> 18104-log-0.txt -> pid-name-index.type
    private String[] separateNameAndType(String fileName) {
        if (fileName == null || fileName.isBlank()) throw new NullPointerException("File name is null or blank.");

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
        if (result[0].isBlank()) throw new NullPointerException("File name is blank.");


        if (result[1] == null) result[1] = "";
        result[1] = result[1].trim();

        if (result[1].length() <= 1) result[1] = "";

        return result;
    }
}
