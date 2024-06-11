package com.ogawa.fico.performance.measuring;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * The StopWatchStatus class depicts the status of a stop watch. All measurements are done in nanoseconds and stored in
 * Instant-variables. All member variables are protected and can only be accessed through public getters. Variants of
 * the getters are provided to return the values as Date, LocalDateTime or Instant. The state of the stop watch is
 * represented by the StopWatchStatus.State enum. The StopWatchStatus class itself is immutable for package-foreign
 * classes because there are no modifying methods. StopWatchStatus can only be created by the StopWatch class, which
 * itself is a subclass of StopWatchStatus.
 */
@ToString
@Getter
public class StopWatchStatus {

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

    /**
     * First start time, which is the time of the first start or if started with pause instead of start, the time of the
     * first resume
     */
    protected Instant firstStartTime;

    /**
     * Recording start time, which is the time of the last start or resume
     */
    protected Instant recordingBeginTime;

    /**
     * Recording end time, which is the time of the last pause or stop
     */
    protected Instant recordingEndTime;

    /**
     * Pause time, which is the time of the last pause
     */
    protected Instant pauseTime;

    /**
     * Stop time, which is the time of the last stop
     */
    protected Instant stopTime;

    /**
     * Accumulated recording time, which is the summed time of all recordings between the first start and the stop or
     * pause time and following resume and stop or pause times
     */
    protected Duration accumulatedRecordingTime;

    /**
     * State of the stop watch
     */
    protected State state;

    /**
     * Creates a stop watch status with a reset stop watch state
     */
    protected StopWatchStatus() {
        reset();
    }

    /**
     * Creates a stop watch status with the given stop watch state
     *
     * @param other Stop watch status to copy
     */
    protected StopWatchStatus(StopWatchStatus other) {
        this.firstStartTime = other.firstStartTime;
        this.recordingBeginTime = other.recordingBeginTime;
        this.recordingEndTime = other.recordingEndTime;
        this.pauseTime = other.pauseTime;
        this.stopTime = other.stopTime;
        this.accumulatedRecordingTime = other.accumulatedRecordingTime;
        this.state = other.state;
    }

    /**
     * Resets the first start- and recording start- and stop-time, busy time and sets the stop watch state to unstarted.
     * The id and name are preserved.
     */
    protected void reset() {
        firstStartTime = null;
        recordingBeginTime = null;
        recordingEndTime = null;
        pauseTime = null;
        stopTime = null;
        accumulatedRecordingTime = Duration.ZERO;
        state = State.UNSTARTED;
    }

    /**
     * Initializes the stop watch status with the values of the given stop watch status
     *
     * @param other Stop watch status to copy
     */
    protected void init(StopWatchStatus other) {
        this.firstStartTime = other.firstStartTime;
        this.recordingBeginTime = other.recordingBeginTime;
        this.recordingEndTime = other.recordingEndTime;
        this.pauseTime = other.pauseTime;
        this.stopTime = other.stopTime;
        this.accumulatedRecordingTime = other.accumulatedRecordingTime;
        this.state = other.state;
    }

    /**
     * Returns the start time, which is the initial start time or the time of the last resume or null if not
     * started/resumed yet
     *
     * @return Start time as Instant
     */
    public Instant getStartTimeInstant() {
        return firstStartTime == null ? null : firstStartTime;
    }

    /**
     * Returns the start time, which is the initial start time or the time of the last resume or null if not
     * started/resumed yet
     *
     * @return Start time as Date
     */
    public Date getStartTimeDate() {
        return firstStartTime == null ? null : Date.from(firstStartTime);
    }

    /**
     * Returns the start time, which is the initial start time or the time of the last resume or null if not
     * started/resumed yet
     *
     * @return Start time as LocalDateTime
     */
    public LocalDateTime getStartTimeLocalDateTime() {
        return firstStartTime == null ? null : instantToLocalDateTime(firstStartTime);
    }

    /**
     * Returns whether the stop watch is paused
     *
     * @return true if paused, else false
     */
    public boolean isPaused() {
        return state == State.PAUSED;
    }

    /**
     * Returns the pause time, which is the time of the last pause or null if not paused yet
     *
     * @return Pause time as Date
     */
    public Date getPauseTimeDate() {
        return pauseTime == null ? null : Date.from(pauseTime);
    }

    /**
     * Returns the pause time, which is the time of the last pause or null if not paused yet
     *
     * @return Pause time as LocalDateTime
     */
    public LocalDateTime getPauseTimeLocalDateTime() {
        return pauseTime == null ? null : instantToLocalDateTime(pauseTime);
    }

    /**
     * Returns the pause time, which is the time of the last pause or null if not paused yet
     *
     * @return Pause time as Instant
     */
    public Instant getPauseTimeInstant() {
        return pauseTime == null ? null : pauseTime;
    }

    /**
     * Returns whether the stop watch is recording
     *
     * @return true if recording, else false
     */
    public boolean isRecording() {
        return state == State.RECORDING;
    }

    /**
     * Returns the recording start time, which is the initial start time or the time of the last resume or null if not
     * started/resumed yet
     *
     * @return Recording start time as Date
     */
    public Date getRecordingStartTimeDate() {
        return recordingBeginTime == null ? null : Date.from(recordingBeginTime);
    }

    /**
     * Returns the recording start time, which is the initial start time or the time of the last resume or null if not
     * started/resumed yet
     *
     * @return Recording start time as LocalDateTime
     */
    public LocalDateTime getRecordingStartTimeLocalDateTime() {
        return recordingBeginTime == null ? null : instantToLocalDateTime(recordingBeginTime);
    }

    /**
     * Returns the recording start time, which is the initial start time or the time of the last resume or null if not
     * started/resumed yet
     *
     * @return Recording start time as Instant
     */
    public Instant getRecordingStartTimeInstant() {
        return recordingBeginTime == null ? null : recordingBeginTime;
    }

    /**
     * Returns whether the stop watch is stopped
     *
     * @return true if stopped, else false
     */
    public boolean isStopped() {
        return state == State.STOPPED;
    }

    /**
     * Returns the stop time, which is the time of the last stop or null if not stopped yet
     *
     * @return Stop time as Date
     */
    public Date getStopTimeDate() {
        return stopTime == null ? null : Date.from(stopTime);
    }

    /**
     * Returns the stop time, which is the time of the last stop or null if not stopped yet
     *
     * @return Stop time as LocalDateTime
     */
    public LocalDateTime getStopTimeLocalDateTime() {
        return stopTime == null ? null : instantToLocalDateTime(stopTime);
    }

    /**
     * Returns the stop time, which is the time of the last stop or null if not stopped yet
     *
     * @return Stop time as Instant
     */
    public Instant getStopTimeInstant() {
        return stopTime == null ? null : stopTime;
    }

    /**
     * Returns the last recorded time, which is the time between the start/last resume and the pause/stop time or
     * current time, if not paused/stopped yet
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
     * Returns the accumulated recording time, which is the summed time of all recordings between the first start and
     * the stop or pause time and following resume and stop or pause times
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
     * Returns the total time, which is the time between the first start (or resume if started with pause instead of
     * start) and the pause/stop time or current time if not paused/stopped yet
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
     * Returns the given instant as LocalDateTime
     *
     * @param instant Instant to convert
     * @return LocalDateTime
     */
    protected static LocalDateTime instantToLocalDateTime(@NonNull Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * Returns the given LocalDateTime as Instant
     *
     * @param localDateTime LocalDateTime to convert
     * @return Instant
     */
    protected static Instant toInstant(@NonNull LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

}
