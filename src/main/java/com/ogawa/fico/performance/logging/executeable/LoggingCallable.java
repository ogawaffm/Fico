package com.ogawa.fico.performance.logging.executeable;

import com.ogawa.fico.performance.logging.ActionLogger;
import java.util.concurrent.Callable;
import lombok.ToString;

@ToString(callSuper = true)
public class LoggingCallable<V> extends LoggingAction implements Callable<V> {

    private final Callable<V> callable;

    public LoggingCallable(ActionLogger actionLogger, Callable<V> callable, String resultName) {
        super(actionLogger, resultName);
        this.callable = callable;
    }

    @Override
    public V call() throws Exception {
        try {
            // announce the step to the progress logger
            actionLogger.announceStep();
            // call the target supplier
            V returnedValue = callable.call();
            registerNamedResultValue(returnedValue);
            // confirm success because no logException was thrown
            confirmSuccess();
            return returnedValue;
        } catch (Exception exception) {
            handleException(exception);
            // never gets here because handleException() always throws an logException
            return null;
        }
    }

}
