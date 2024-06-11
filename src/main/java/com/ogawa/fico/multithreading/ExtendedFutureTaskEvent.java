package com.ogawa.fico.multithreading;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public enum ExtendedFutureTaskEvent {
    SUBMITTED, STARTED, SUCCEEDED, CANCELLED, FAILED, INTERRUPTED;

    static public ExtendedFutureTaskEvent getState(ExtendedFutureTask<?> extendedFutureTask) {

        if (extendedFutureTask.isDone()) {
            try {
                // getFromSupplier result immediately
                Object ignore = extendedFutureTask.get(0, TimeUnit.NANOSECONDS);
                return SUCCEEDED;
            } catch (CancellationException cancellationException) {
                return CANCELLED;
            } catch (ExecutionException executionException) {
                return FAILED;
            } catch (InterruptedException interruptedException) {
                // ignore/reset
                Thread.currentThread().interrupt(); // TODO: is this necessary/correct?
                return INTERRUPTED;
            } catch (TimeoutException e) {
                // should not happen
                throw new RuntimeException(e);
            }
        } else {
            if (extendedFutureTask.getStartTime() == null) {
                return SUBMITTED;
            } else {
                return STARTED;
            }
        }
    }
}
