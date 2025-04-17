package utility;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;
import lombok.NonNull;

public class TestUtils {

    public static void waitMillis(int millisToWait) {

        if (millisToWait < 0) {
            throw new IllegalArgumentException("millisToWait must be greater than or equal to 0");
        }

        if (millisToWait > 0) {
            try {
                Thread.sleep(millisToWait);
            } catch (InterruptedException interruptedException) {
                throw new RuntimeException(interruptedException);
            }
        }
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

    public static void waitUntilNonNull(@NonNull AtomicReference referenceToCheck) {
        while (referenceToCheck.get() == null) {
            try {
                //noinspection BusyWait
                Thread.sleep(100);
            } catch (InterruptedException interruptedException) {
                throw new RuntimeException(interruptedException);
            }
        }
    }

}
