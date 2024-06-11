package com.ogawa.fico.performance.logging;

import java.time.Duration;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import lombok.NonNull;

/**
 * Units to measure performance.
 *
 * @implSpec This is a final, immutable and thread-safe enum.
 */
public enum PerformanceChronoUnit implements TemporalUnit {

    NANOS("Nanos", "ns", Duration.ofNanos(1L)),
    MICROS("Micros", "µs", Duration.ofNanos(1000L)),
    MILLIS("Millis", "ms", Duration.ofNanos(1000_000L)),
    SECONDS("Seconds", "s", Duration.ofSeconds(1L)),
    MINUTES("Minutes", "m", Duration.ofSeconds(60L)),
    HOURS("Hours", "h", Duration.ofSeconds(3600L)),
    DAYS("Days", "d", Duration.ofSeconds(86400L));

    private final String name;
    private final String symbol;
    private final Duration duration;

    PerformanceChronoUnit(String name, String symbol, Duration duration) {
        this.name = name;
        this.symbol = symbol;
        this.duration = duration;
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    @Override
    public boolean isDurationEstimated() {
        return this.equals(DAYS);
    }

    @Override
    public boolean isDateBased() {
        return this.equals(DAYS);
    }

    @Override
    public boolean isTimeBased() {
        return !this.equals(DAYS);
    }

    @Override
    public boolean isSupportedBy(Temporal temporal) {
        return temporal.isSupported(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R extends Temporal> R addTo(R temporal, long amount) {
        return (R) temporal.plus(amount, this);
    }

    @Override
    public long between(Temporal temporal1Inclusive, Temporal temporal2Exclusive) {
        return temporal1Inclusive.until(temporal2Exclusive, this);
    }

    @Override
    public String toString() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getUnit(@NonNull Number numberOfUnits) {
        return Math.abs(numberOfUnits.doubleValue()) == 1 ? name.substring(0, name.length() - 1) : name;
    }

}
