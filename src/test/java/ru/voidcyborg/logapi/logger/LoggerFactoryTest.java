package ru.voidcyborg.logapi.logger;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.fail;

class LoggerFactoryTest {

    @Test
    void setSettings() {
      /*  try {
            LoggerFactory.setSettings("/assets/logapi/logger.settings", true);
        }catch (Exception e){
            e.printStackTrace();
            fail();
        }*/

    }

    @Test
    void setSettings2() {
        try {
            LoggerFactory.setSettings("logger.settings", false);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        Logger logger = LoggerFactory.getLoggerGroup("Security").getLogger();

        Random random = new Random();


        logger.info("BBBBB");
        for (int i = 0; i < 5000; i++) {
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
    }
}