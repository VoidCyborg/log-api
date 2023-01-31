package ru.voidcyborg.logapi.logger;

import org.junit.jupiter.api.Test;
import ru.voidcyborg.logapi.settings.SettingsInitException;

import static org.junit.jupiter.api.Assertions.fail;

class LoggerFactoryTest {

  /*  static {
        try {
            LoggerFactory.setSettings(LoggerFactoryTest.class.getResourceAsStream("/assets/ru/voidcyborg/logapi/logger.settings"));
        } catch (SettingsInitException e) {
            e.printStackTrace();
        }
    }
*/

    @Test
    void setSettings() {
        try {
            LoggerFactory.setSettings(LoggerFactoryTest.class.getResourceAsStream("/assets/ru/voidcyborg/logapi/logger.settings"));


            Logger logger = LoggerFactory.getLoggerGroup("Security").getLogger();
            logger.info("BBBBB");
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }


    }

    @Test
    void setSettings2() {
     /*   try {
            LoggerFactory.setSettings("logger.settings", false);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        HashMap<String, String> settings = new HashMap<>();
        settings.put("maxFileSize", "1024_000");
        settings.put("fileName", "bebralog.txt");
        settings.put("folderPath", "logs");
        settings.put("maxFiles", "10");

        Appender appender = new RollingFileAppender();
        try {
            appender.parseSettings(settings);
        } catch (Exception ignore) {
        }

        Logger logger = LoggerFactory.getLoggerGroup("Security").addAppender(appender).getLogger();

        Random random = new Random();


        logger.info("BBBBB");
        for (int i = 0; i < 500_000; i++) {
            logger.warn(String.valueOf(random.nextLong()));
        }

        logger.info("Bebdra 12321541");
        logger.fatal("Beqbra fafsc21111");
        logger.trace("Bebrqdda mazxczxczxcssage888888888");
        logger.debug("Bebra mascaessage3333333");
        logger.error("Bedwqbra maxcascessage1111111111");
        logger.warn("Bebra me222ssage");
        logger.info("AAAAA");


        try {
            Thread.sleep(60_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
 */

    }


}