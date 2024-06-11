package com.ogawa.fico.performance.measuring;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import lombok.NonNull;
import lombok.ToString;

// @formatter:off
/**
 * I1 StopWatch can be started, paused, resumed and stopped.
 * <p></p>
 * The time between start and pause/stop and also
 * between resume and pause/stop is recorded.
 * <p></p>
 * The time from start/first resume (when directly paused logAfter
 * creation) to pause/stop or current time if not paused/stopped yet is recorded and available from
 * {@link #getTotalTime()}.
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
public class StopWatchOld {

    public enum State {

        /**
         * Initial state, stop watch is not started yet
         */
        UNSTARTED,

        /**
         * StopWatch is started and running or resumed logAfter a pause
         */
        RECORDING,

        /**
         * Timer is paused right away logAfter creation or logAfter a start
         */
        PAUSED,

        /**
         * Timer is stopped and cannot be resumed but started again
         */
        STOPPED;

        public String getVerb() {
            return name().toLowerCase();
        }
    }

    private Instant firstStartTime;
    protected Instant recordingBeginTime;
    protected Instant recordingEndTime;
    protected Instant pauseTime;
    protected Instant stopTime;
    private Duration accumulatedRecordingTime;
    private State state;
    private String name;
    private final long id;

    /* *********************************************************************************************************** */
    /* ********************************************** Construction *********************************************** */
    /* *********************************************************************************************************** */

    private final static AtomicLong idSequence = new AtomicLong(0);

    private StopWatchOld(long id, String name) {
        this.id = id;
        this.name = name;
        reset();
    }

    static public StopWatchOld create() {
        long id = idSequence.incrementAndGet();
        return new StopWatchOld(id, "Timer-" + id);
    }

    static public StopWatchOld create(@NonNull String name) {
        return new StopWatchOld(idSequence.incrementAndGet(), name);
    }

    /**
     * Returns the name of the stop watch
     *
     * @return name
     */
    public String getName() {
        return name;
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
     * Returns the state of the stop watch
     *
     * @return state
     */
    public State getState() {
        return state;
    }

    /**
     * Starts the stop watch with the current time as start-time and resets the end-time
     */
    public void start() {
        start(Instant.now());
    }

    /**
     * Starts the stop watch with a given start-time and resets the end-time
     *
     * @param startTime time to recognize as start time
     */
    public void start(@NonNull LocalDateTime startTime) {
        start(toInstant(startTime));
    }

    public void start(@NonNull Date startTime) {
        start(startTime.toInstant());
    }

    /**
     * Starts the stop watch with the start-time of another stop watch and resets the end-time
     *
     * @param otherStopWatch other stop watch
     */
    public void start(@NonNull StopWatchOld otherStopWatch) {
        if (otherStopWatch.state != State.RECORDING) {
            throw new IllegalStateException(
                "Cannot synchronize start, other stop watch is " + otherStopWatch.state.getVerb());
        }
        start(otherStopWatch.recordingBeginTime);
    }

    /**
     * Starts or restarts the stop watch with a given start-time and resets the end-time
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
    }

    public boolean isStartable() {
        return state == State.UNSTARTED || state == State.STOPPED;
    }

    public Date getStartTimeDate() {
        return firstStartTime == null ? null : Date.from(firstStartTime);
    }

    public LocalDateTime getStartTimeLocalDateTime() {
        return firstStartTime == null ? null : instantToLocalDateTime(firstStartTime);
    }

    public Instant getStartTimeInstant() {
        return firstStartTime == null ? null : firstStartTime;
    }

    public boolean isRecording() {
        return state == State.RECORDING;
    }

    public void pause() {
        pause(Instant.now());
    }

    public void pause(@NonNull LocalDateTime pauseTime) {
        pause(toInstant(pauseTime));
    }

    public void pause(@NonNull Date pauseTime) {
        pause(pauseTime.toInstant());
    }

    public Date getPauseTimeDate() {
        return pauseTime == null ? null : Date.from(pauseTime);
    }

    public LocalDateTime getPauseTimeLocalDateTime() {
        return pauseTime == null ? null : instantToLocalDateTime(pauseTime);
    }

    public Instant getPauseTimeInstant() {
        return pauseTime == null ? null : pauseTime;
    }

    public void pause(@NonNull StopWatchOld otherStopWatch) {
        if (otherStopWatch.state != State.RECORDING) {
            throw new IllegalStateException(
                "Cannot synchronize pause, other stop watch is " + otherStopWatch.state.getVerb());
        }
        pause(otherStopWatch.pauseTime);
    }

    public void pause(@NonNull Instant pauseTime) {
        if (!isPausable()) {
            throw new IllegalStateException("Cannot pause, stop watch is " + state.getVerb());
        }
        if (recordingBeginTime != null && pauseTime.isBefore(recordingBeginTime)) {
            throw new IllegalStateException("Pause time is logBefore start time");
        }

        // save pause time to be able to pause other stop watches using the same pause time
        this.pauseTime = pauseTime;
        recordingEndTime = pauseTime;

        // Is this a pause logAfter a recording (start/resume) and nota pause directly logAfter creation?
        if (recordingBeginTime != null) {
            // calculate the time between the last start/resume and the pause time and plus it to the measured time
            accumulatedRecordingTime = accumulatedRecordingTime.plus(
                Duration.between(recordingBeginTime, pauseTime));
        }

        state = State.PAUSED;
    }

    public boolean isPausable() {
        return state == State.RECORDING || state == State.UNSTARTED;
    }

    public boolean isPaused() {
        return state == State.PAUSED;
    }

    public void resume() {
        resume(Instant.now());
    }

    public void resume(@NonNull LocalDateTime resumedTime) {
        resume(toInstant(resumedTime));
    }

    public void resume(@NonNull Date resumedTime) {
        resume(resumedTime.toInstant());
    }

    public void resume(@NonNull StopWatchOld otherStopWatch) {
        if (otherStopWatch.state != State.PAUSED) {
            throw new IllegalStateException(
                "Cannot synchronize resume, other stop watch is " + otherStopWatch.state.getVerb());
        } else if (otherStopWatch.recordingBeginTime == null) {
            throw new IllegalStateException(
                "Cannot synchronize resume, other stop watch has no recording start time");
        }
        resume(otherStopWatch.recordingBeginTime);
    }

    public void resume(@NonNull Instant resumedTime) {
        if (!isResumable()) {
            throw new IllegalStateException("Cannot resume, stop watch is " + state.getVerb());
        }
        recordingBeginTime = resumedTime;
        // resume without a prior start?
        if (firstStartTime == null) {
            // yes, record the first start time
            firstStartTime = recordingBeginTime;
        }
        state = State.RECORDING;
    }

    /**
     * Returns the recording start time, which is the initial start time or the time of the last resume
     *
     * @return Recording start time as Date
     */
    public Date getRecordingStartTimeDate() {
        return recordingBeginTime == null ? null : Date.from(recordingBeginTime);
    }

    /**
     * Returns the recording start time, which is the initial start time or the time of the last resume
     *
     * @return Recording start time as LocalDateTime
     */
    public LocalDateTime getRecordingStartTimeLocalDateTime() {
        return recordingBeginTime == null ? null : instantToLocalDateTime(recordingBeginTime);
    }

    /**
     * Returns the recording start time, which is the initial start time or the time of the last resume
     *
     * @return Recording start time as Instant
     */
    public Instant getRecordingStartTimeInstant() {
        return recordingBeginTime == null ? null : recordingBeginTime;
    }

    public boolean isResumable() {
        return state == State.PAUSED;
    }

    public Date getStopTimeDate() {
        return stopTime == null ? null : Date.from(stopTime);
    }

    public LocalDateTime getStopTimeLocalDateTime() {
        return stopTime == null ? null : instantToLocalDateTime(stopTime);
    }

    public Instant getStopTimeInstant() {
        return stopTime == null ? null : stopTime;
    }

    /**
     * Stops the stop watch immediately
     */
    public void stop() {
        stop(Instant.now());
    }

    /**
     * Stops the stop watch with a given the stop-time
     *
     * @param stopTime time to recognize as stop time
     */
    public void stop(@NonNull LocalDateTime stopTime) {
        stop(toInstant(stopTime));
    }

    public void stop(@NonNull Date stopTime) {
        stop(stopTime.toInstant());
    }

    /**
     * Stops the stop watch with the stop-time of another stop watch
     *
     * @param otherStopWatch other stop watch
     */
    public void stop(@NonNull StopWatchOld otherStopWatch) {
        if (otherStopWatch.state != State.STOPPED) {
            throw new IllegalStateException(
                "Cannot synchronize stop, other stop watch is " + otherStopWatch.state.getVerb());
        }
        stop(otherStopWatch.stopTime);
    }

    public void stop(@NonNull Instant stopTime) {
        if (!isStoppable()) {
            throw new IllegalStateException("Cannot stop, stop watch is " + state.getVerb());
        }
        if (stopTime.isBefore(recordingBeginTime)) {
            throw new IllegalStateException("Stop time is logBefore start time");
        }

        this.stopTime = stopTime;
        recordingEndTime = stopTime;
        accumulatedRecordingTime = accumulatedRecordingTime.plus(Duration.between(recordingBeginTime, stopTime));
        state = State.STOPPED;
    }

    public boolean isStoppable() {
        return state == State.RECORDING || state == State.PAUSED;
    }

    public boolean isStopped() {
        return state == State.STOPPED;
    }

    /**
     * The last recorded time is the time between the start/last resume and the pause/stop time or current time if not
     * paused/stopped yet
     *
     * @return last recorded time
     */
    public Duration getLastRecordedTime() {
        if (state == State.RECORDING) {
            return Duration.between(recordingBeginTime, Instant.now());
        } else {
            return Duration.between(recordingBeginTime, recordingEndTime);
        }
    }

    /**
     * The accumulated recording time is the summed time of all recordings between the first start and the stop or pause
     * time and following resume and stop or pause times
     *
     * @return accumulated recording time
     */
    public Duration getAccumulatedRecordedTime() {
        if (state == State.RECORDING) {
            return accumulatedRecordingTime.plus(Duration.between(firstStartTime, Instant.now()));
        } else {
            return accumulatedRecordingTime;
        }
    }

    /**
     * The total time is the time between the first start (or resume if started with pause instead of start) and the
     * pause/stop time or current time if not paused/stopped yet
     *
     * @return total time
     */
    public Duration getTotalTime() {
        if (state == State.RECORDING) {
            return Duration.between(firstStartTime, Instant.now());
        } else {
            // not started/resumed at all?
            if (firstStartTime == null) {
                return Duration.ZERO;
            } else {
                return Duration.between(firstStartTime, recordingEndTime);
            }
        }
    }

    /**
     * Creates and return a clone of the Timer-instance, which has a different id and a generic name
     *
     * @return clone Timer-instance
     */
    @SuppressWarnings("MethodDoesntCallSuperMethod")

    public StopWatchOld clone() {
        StopWatchOld clone = create();
        clone.firstStartTime = this.firstStartTime;
        clone.recordingBeginTime = this.recordingBeginTime;
        clone.stopTime = this.stopTime;
        clone.accumulatedRecordingTime = this.accumulatedRecordingTime;
        clone.state = this.state;
        return clone;
    }

    /**
     * Resets the first start- and recording start- and stop-time, busy time and sets the stop watch state to unstarted.
     * The id and name are preserved.
     */
    private void reset() {
        firstStartTime = null;
        recordingBeginTime = null;
        stopTime = null;
        accumulatedRecordingTime = Duration.ZERO;
        this.state = State.UNSTARTED;
    }

    private static LocalDateTime instantToLocalDateTime(@NonNull Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    private static Instant toInstant(@NonNull LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

}
