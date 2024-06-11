package com.ogawa.fico;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.Date;

public abstract class CharacteristicsConsumer<C> {

    private Timestamp timeStarted;
    private Timestamp timeFinished;

    public void open() {
        timeStarted = new Timestamp(new Date().getTime());
    }

    ;

    abstract public void accept(C characteristics);

    public void close() {
        timeFinished = new Timestamp(new Date().getTime());
    }

    ;

    public Timestamp getTimeStarted() {
        return timeStarted;
    }

    public Timestamp getTimeFinished() {
        return timeFinished;
    }

    public Duration getDuration() {
        if (getTimeFinished() == null) {
            return null;
        } else {
            return Duration.between(getTimeStarted().toInstant(), getTimeFinished().toInstant());
        }
    }

}
