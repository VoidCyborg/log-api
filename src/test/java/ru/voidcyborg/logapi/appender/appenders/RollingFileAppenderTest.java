package ru.voidcyborg.logapi.appender.appenders;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.fail;

class RollingFileAppenderTest {

    @Test
    void separateNameAndType() {
        RollingFileAppender appender = new RollingFileAppender();


        String[] names = new String[]{
                null,
                "veqvq.rrr",
                "bebralog.txt",
                "f12rf",
                "",
                " . "
        };

        Method method;
        try {
            method = appender.getClass().getDeclaredMethod("separateNameAndType", String.class);
            method.setAccessible(true);
        } catch (Exception e) {
            fail();
            return;
        }

        for (String s : names) {
            try {
                System.out.println(Arrays.toString((String[])method.invoke(appender, s)));
            } catch (Exception e) {
                fail();
            }
        }
    }
}