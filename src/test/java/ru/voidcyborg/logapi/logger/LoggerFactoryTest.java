package ru.voidcyborg.logapi.logger;

import com.sun.net.httpserver.Authenticator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
    void setSettings2(){
        try {
            LoggerFactory.setSettings("logger.settings", false);
        }catch (Exception e){
            e.printStackTrace();
            fail();
        }
    }
}