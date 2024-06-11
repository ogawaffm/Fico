package com.ogawa.fico.performance.measuring;


import java.time.Duration;
import java.time.temporal.TemporalUnit;
import lombok.NonNull;

/**
 * Immutable Progress class to measure the progress of a process
 */
public class Progress {

    /**
     * Processing time
     */
    private final Duration processingTime;

    /**
     * Number of processed units
     */
    private final long processedUnits;

    /**
     * Zero Progress-instance
     */
    public static final Progress ZERO = new Progress(Duration.ZERO, 0L);

    /**
     * Factory method to create a new Progress-instance with the given processing time and processed units
     *
     * @param processingTime Processing time
     * @param processedUnits Number of processed units
     * @return Progress-instance
     */
    public static Progress of(@NonNull Duration processingTime, long processedUnits) {
        return new Progress(processingTime, processedUnits);
    }

    /**
     * Creates a new Progress-instance with the given processing time and processed units
     *
     * @param processingTime Processing time
     * @param processedUnits Number of processed units
     */
    private Progress(Duration processingTime, long processedUnits) {
        this.processingTime = processingTime;
        this.processedUnits = processedUnits;
    }

    /**
     * Returns the time between start- and stop-time
     *
     * @return Duration
     */
    public Duration getProcessingTime() {
        return processingTime;
    }

    /**
     * Returns the time for a certain amount of units
     *
     * @param perUnits number of units
     * @return time for perUnits amount of units
     */
    public Duration getProcessingTime(long perUnits) {
        if (processingTime.isZero() || processedUnits == 0L) {
            return Duration.ZERO;
        }
        return processingTime.dividedBy(perUnits);
    }

    /**
     * Returns the number of units processed
     *
     * @return Number of units
     */
    public long getProcessedUnits() {
        return processedUnits;
    }

    /**
     * Returns the number of units processed per duration of a certain amount of ChronoUnits
     *
     * @param temporalUnit ChronoUnit
     * @return Number of units per amount of ChronoUnits
     */
    public long getProcessedUnitsPer(long amount, @NonNull TemporalUnit temporalUnit) {
        return getProcessedUnitsPer(Duration.of(amount, temporalUnit));
    }

    /**
     * Returns the number of units processed per duration
     *
     * @param duration Duration
     * @return Number of units per duration
     */
    public long getProcessedUnitsPer(@NonNull Duration duration) {
        long divisor = getProcessingTime().dividedBy(duration);
        return divisor == 0 ? 0 : getProcessedUnits() / divisor;
    }

    /**
     * Adds processed units to the progress
     *
     * @param processedUnits number of processed units to add
     * @return Resulting Progress-instance
     */
    public Progress plus(long processedUnits) {
        return new Progress(processingTime, this.processedUnits + processedUnits);
    }

    /**
     * Adds processing time to the progress
     *
     * @param additionalProcessingTime processing time to add
     * @return Resulting Progress-instance
     */
    public Progress plus(Duration additionalProcessingTime) {
        return new Progress(processingTime.plus(additionalProcessingTime), processedUnits);
    }

    /**
     * Adds processing time and processed units to the progress
     *
     * @param additionalProcessingTime processing time to add
     * @param additionalProcessedUnits number of processed units to add
     * @return Resulting Progress-instance
     */

    public Progress plus(@NonNull Duration additionalProcessingTime, long additionalProcessedUnits) {
        return new Progress(
            this.processingTime.plus(additionalProcessingTime),
            processedUnits + additionalProcessedUnits
        );
    }

    /**
     * Adds the processing time and processed units of the other Progress-instance to the progress
     *
     * @param otherProgress Progress-instance to add
     * @return Resulting Progress-instance
     */
    public Progress plus(@NonNull Progress otherProgress) {
        return new Progress(
            processingTime.plus(otherProgress.processingTime),
            processedUnits + otherProgress.processedUnits
        );
    }

    /**
     * Resets the processing time and units of the Progress
     *
     * @return Reset Progress
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Progress)) {
            return false;
        }
        Progress progress = (Progress) o;
        return this.processedUnits == progress.processedUnits && this.processingTime.equals(progress.processingTime);
    }

    @Override
    public String toString() {
        return "Progress: " + getProcessedUnits() + " units in " + getProcessingTime();
    }

}
