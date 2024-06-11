package com.ogawa.fico.multithreading;

import java.time.LocalDateTime;

public class ThreadUtils {

    public static int getCores() {
        return Runtime.getRuntime().availableProcessors();
    }

    public static void waitSeconds(int secondsToWait) {

        if (secondsToWait < 0) {
            throw new IllegalArgumentException("secondsToWait must be greater than or equal to 0");
        }

        if (secondsToWait > 0) {
            try {
                Thread.sleep(secondsToWait * 1000L);
            } catch (InterruptedException interruptedException) {
                throw new RuntimeException(interruptedException);
            }
        }
    }

    public static void workSeconds(int secondsToWork) {

        if (secondsToWork < 0) {
            throw new IllegalArgumentException("secondsToWork must be greater than or equal to 0");
        }

        if (secondsToWork > 0) {
            LocalDateTime waitUntil = LocalDateTime.now().plusSeconds(secondsToWork);

            while (LocalDateTime.now().isBefore(waitUntil)) {
                // do nothing
            }
        }

    }

}
