package com.ogawa.fico.performance.measuring;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import lombok.NonNull;
import lombok.ToString;

// @formatter:off
/**
 * The StopWatch class is a simple stop watch to measure time. It extends the {@link StopWatchStatus} class, which
 * depicts only the status (variables) of the stop watch. The status of a StopWatch can be preserved as a
 * StopWatchStatus instance using the {@link #getPreservedStatus()} method.
 * <p></p>
 * StopWatch extends StopWatchStatus by the following actions: start, pause, resume and stop.
 * <p></p>
 * The time between start and pause/stop and also between resume and pause/stop is recorded.
 * <p></p>
 * The time from start/first resume (when directly paused logAfter creation) to pause/stop or current time if not
 * paused/stopped yet is recorded and available from {@link #getTotalTime()}.
 * <p></p>
 * The last recorded time is available from {@link #getLastRecordedTime()}. It is equal to the total time if the
 * stop watch was not paused. If the stop watch was paused, it is the time between the last start/resume and the
 * pause/stop time.
 * <p></p>
 * The accumulated recording time is the summed time of all recordings and is available from
 * {@link StopWatch#getAccumulatedRecordedTime()}.
 * <p></p>
<pre>
                            ╭────────── start ───────────╮
                            ▼                            │
                      ╭─────┴─────╮                ╭─────┴─────╮
                ╭────▶┤ RECORDING ├─────┬── stop ─▶┤  STOPPED  │
                │     ╰─────┬─────╯     │          ╰─────┬─────╯
              start         ▲         pause              ▲
                │           │           │                │
  ╭───────────╮ │         resume        ▼               stop
─▶│ UNSTARTED ├─┤           │     ╭─────┴─────╮          │
  ╰───────────╯ │           ╰─────┤  PAUSED   ├──────────╯
                │                 ╰─────┬─────╯
                │                       ▲
                ╰──────── pause ────────╯
</pre> */
// @formatter:logEvent

@ToString(callSuper = true)
public class StopWatch extends StopWatchStatus {

    /* *********************************************************************************************************** */
    /* ********************************************** Construction *********************************************** */
    /* *********************************************************************************************************** */

    /**
     * Sequence to generate the id of the stop watches
     */
    private final static AtomicLong idSequence = new AtomicLong(0);

    /**
     * Name of the stop watch
     */
    private String name;

    private long recordingCount = 0;

    /**
     * unique id of the stop watch
     */
    final private long id;

    /**
     * Creates a new StopWatch-instance with a given id and name
     *
     * @param id   id
     * @param name name
     */
    private StopWatch(long id, String name) {
        this.id = id;
        this.name = name;
        reset();
    }

    /**
     * Creates and returns a new StopWatch-instance with a generic name based logEvent the id
     *
     * @return StopWatch-instance
     */
    static public StopWatch create() {
        long id = idSequence.incrementAndGet();
        return new StopWatch(id, "StopWatch-" + id);
    }

    /**
     * Creates and returns a new StopWatch-instance with a given name
     *
     * @param name new name
     * @return StopWatch-instance
     */
    static public StopWatch create(@NonNull String name) {
        return new StopWatch(idSequence.incrementAndGet(), name);
    }

    /**
     * Sets the name of the stop watch
     *
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns whether the stop watch can be started, which is the case if it was not started yet or is in stopped
     * state
     *
     * @return true if the stop watch can be started, else false
     */
    public boolean isStartable() {
        return state == State.UNSTARTED || state == State.STOPPED;
    }

    /**
     * Starts the stop watch with the current time as start-time and resets the end-time
     */
    public void start() {
        start(Instant.now());
    }

    /**
     * Starts or restarts the stop watch with a given start-time and resets the end-time. If the stop watch is not
     * {@link #isStartable() startable}, an {@link IllegalStateException} is thrown.
     *
     * @param startTime time to recognize as start time
     */
    public void start(@NonNull Instant startTime) {
        if (!isStartable()) {
            throw new IllegalStateException("Cannot start, stop watch is " + state.getVerb());
        }
        this.firstStartTime = startTime;
        this.recordingBeginTime = startTime;
        this.stopTime = null;
        this.accumulatedRecordingTime = Duration.ZERO;
        this.state = State.RECORDING;
        recordingCount++;
    }

    /**
     * Returns whether the stop watch can be paused, which is the case if it is recording or not started yet
     *
     * @return true if the stop watch can be paused, else false
     */
    public boolean isPausable() {
        return state == State.RECORDING || state == State.UNSTARTED;
    }

    /**
     * Pauses the stop watch with the current time as pause-time. If the stop watch is not
     * {@link #isPausable() pausable} or the pause time is logBefore the start time, an {@link IllegalStateException} is
     * thrown.
     */
    public void pause() {
        pause(Instant.now());
    }

    /**
     * Pauses the stop watch with a given pause-time. If the stop watch is not {@link #isPausable() pausable} or the
     * pause time is logBefore the start time, an {@link IllegalStateException} is thrown.
     *
     * @param pauseTime time to recognize as pause time
     */
    public void pause(@NonNull Instant pauseTime) {
        if (!isPausable()) {
            throw new IllegalStateException("Cannot pause, stop watch is " + state.getVerb());
        }
        if (recordingBeginTime != null && pauseTime.isBefore(recordingBeginTime)) {
            throw new IllegalStateException("Pause time is logBefore start time");
        }

        // save pause time to be able to pause other stop watches using the same pause time
        this.pauseTime = pauseTime;

        // Was this a pause of a real recording (after start/resume) and not a pause directly after creation?
        if (recordingBeginTime != null) {
            recordingEndTime = pauseTime;
            // calculate the time between the last start/resume and the pause time and plus it to the measured time
            accumulatedRecordingTime = accumulatedRecordingTime.plus(
                Duration.between(recordingBeginTime, pauseTime));
        }

        state = State.PAUSED;
    }

    /**
     * Returns whether the stop watch can be resumed, which is the case if it is paused
     *
     * @return true if the stop watch can be resumed, else false
     */
    public boolean isResumable() {
        return state == State.PAUSED;
    }

    /**
     * Resumes the stop watch with the current time as resume-time. If the stop watch is not
     * {@link #isResumable() resumable} or the resume time is logBefore the start time, an {@link IllegalStateException} is
     * thrown.
     */
    public void resume() {
        resume(Instant.now());
    }

    /**
     * Resumes the stop watch with a given resume-time. If the stop watch is not {@link #isResumable() resumable} or the
     * resume time is logBefore the start time, an {@link IllegalStateException} is thrown.
     *
     * @param resumedTime time to recognize as resume time
     */
    public void resume(@NonNull Instant resumedTime) {
        if (!isResumable()) {
            throw new IllegalStateException("Cannot resume, stop watch is " + state.getVerb());
        }
        // resume without a prior start?
        if (firstStartTime == null) {
            // yes, record the first start time
            firstStartTime = recordingBeginTime;
        } else if (resumedTime.isBefore(firstStartTime)) {
            throw new IllegalStateException("Resume time is logBefore start time");
        }
        recordingBeginTime = resumedTime;
        state = State.RECORDING;
        recordingCount++;
    }

    /**
     * Returns whether the stop watch can be stopped, which is the case if it is recording or paused
     *
     * @return true if the stop watch can be stopped, else false
     */
    public boolean isStoppable() {
        return state == State.RECORDING || state == State.PAUSED;
    }

    /**
     * Stops the stop watch with the current time as stop-time. If the stop watch is not
     * {@link #isStoppable() stoppable} or the stop time is logBefore the start time, an {@link IllegalStateException} is
     * thrown.
     */
    public void stop() {
        stop(Instant.now());
    }

    /**
     * Stops the stop watch with a given stop-time. If the stop watch is not {@link #isStoppable() stoppable} or the
     * stop time is logBefore the start time, an {@link IllegalStateException} is thrown.
     *
     * @param stopTime time to recognize as stop time
     */
    public void stop(@NonNull Instant stopTime) {
        if (!isStoppable()) {
            throw new IllegalStateException("Cannot stop, stop watch is " + state.getVerb());
        }
        if (recordingBeginTime != null && stopTime.isBefore(recordingBeginTime)) {
            throw new IllegalStateException("Stop time is logBefore start time");
        }
        this.stopTime = stopTime;

        // stop without a prior start because first state was paused (directly after creation)?
        if (recordingBeginTime == null) {
            recordingEndTime = null;
            accumulatedRecordingTime = Duration.ZERO;
        } else {
            recordingEndTime = stopTime;
            accumulatedRecordingTime = accumulatedRecordingTime.plus(Duration.between(recordingBeginTime, stopTime));
        }
        state = State.STOPPED;

    }

    /**
     * Creates and return a clone of the Timer-instance, which has a different id and a generic name
     *
     * @return clone Timer-instance
     */
    @SuppressWarnings("MethodDoesntCallSuperMethod")

    public StopWatch clone() {
        StopWatch clone = create();
        clone.init(this);
        return clone;
    }

    /**
     * Returns a preserved status of the stop watch. If the stop watch is recording, it is stopped and the stop time is
     * set to the current time.
     *
     * @return preserved status
     */
    public StopWatchStatus getPreservedStatus() {
        if (state == State.RECORDING) {
            Instant stopTime = Instant.now();
            StopWatchStatus stopWatchStatus = new StopWatchStatus(this);
            stopWatchStatus.stopTime = stopTime;
            stopWatchStatus.state = State.STOPPED;
            return stopWatchStatus;
        } else {
            return new StopWatchStatus(this);
        }
    }

    public long getRecordingCount() {
        return recordingCount;
    }

    /* *********************************************************************************************************** */
    /* ****************************************** Date method variants ******************************************* */
    /* *********************************************************************************************************** */

    /**
     * Date argument based implementation of the {@link #start(Instant)} method
     *
     * @param startTime time to recognize as start time
     */
    public void start(@NonNull Date startTime) {
        start(startTime.toInstant());
    }

    /**
     * Date argument based implementation of the {@link #pause(Instant)} method
     *
     * @param pauseTime Time to recognize as pause time
     */
    public void pause(@NonNull Date pauseTime) {
        pause(pauseTime.toInstant());
    }

    /**
     * Date argument based implementation of the {@link #resume(Instant)} method
     *
     * @param resumedTime Time to recognize as resume time
     */
    public void resume(@NonNull Date resumedTime) {
        resume(resumedTime.toInstant());
    }

    /**
     * Date argument based implementation of the {@link #stop(Instant)} method
     *
     * @param stopTime Time to recognize as stop time
     */
    public void stop(@NonNull Date stopTime) {
        stop(stopTime.toInstant());
    }

    /* *********************************************************************************************************** */
    /* ************************************** LocalDateTime method variants ************************************** */
    /* *********************************************************************************************************** */

    /**
     * LocalDateTime argument based implementation of the {@link #start(Instant)} method
     *
     * @param startTime Time to recognize as start time
     */
    public void start(@NonNull LocalDateTime startTime) {
        start(toInstant(startTime));
    }

    /**
     * LocalDateTime argument based implementation of the {@link #pause(Instant)} method
     *
     * @param pauseTime Time to recognize as pause time
     */
    public void pause(@NonNull LocalDateTime pauseTime) {
        pause(toInstant(pauseTime));
    }

    /**
     * LocalDateTime argument based implementation of the {@link #resume(Instant)} method
     *
     * @param resumedTime Time to recognize as resume time
     */
    public void resume(@NonNull LocalDateTime resumedTime) {
        resume(toInstant(resumedTime));
    }

    /**
     * LocalDateTime argument based implementation of the {@link #stop(Instant)} method
     *
     * @param stopTime Time to recognize as stop time
     */
    public void stop(@NonNull LocalDateTime stopTime) {
        stop(toInstant(stopTime));
    }

}
