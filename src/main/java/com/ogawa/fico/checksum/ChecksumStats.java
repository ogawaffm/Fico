package com.ogawa.fico.checksum;

import java.time.Duration;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NonNull;

public class ChecksumStats implements Comparable<ChecksumStats> {

    private ChecksumBuilder<?> checksumBuilder;
    private String algorithmName;
    @Getter
    private byte[] binaryChecksum;
    @Getter
    private LocalDateTime timeStarted;
    @Getter
    private LocalDateTime timeFinished;

    public ChecksumStats(@NonNull ChecksumBuilder<?> checksumBuilder) {
        this.checksumBuilder = checksumBuilder;
    }

    /**
     * Returns the algorithm name. If finished, the stored name is returned else this of the still active checksum
     * builder
     *
     * @return the algorithm name
     */

    public String getAlgorithmName() {
        return isFinished() ? algorithmName : this.checksumBuilder.getAlgorithmName();
    }

    public String getHexChecksum() {
        return ChecksumBuilder.getBytesToHex(binaryChecksum);
    }

    public void start() {
        timeStarted = LocalDateTime.now();
    }

    public void finish() {
        timeFinished = LocalDateTime.now();
        binaryChecksum = checksumBuilder.getByteChecksum();
        algorithmName = checksumBuilder.getAlgorithmName();

        // release reference to the checksum builder
        checksumBuilder = null;
    }

    public boolean isFinished() {
        return timeFinished == null;
    }

    public Duration getDuration() {
        if (!isFinished()) {
            return null;
        } else {
            return Duration.between(getTimeStarted(), getTimeFinished());
        }
    }

    @Override
    public int compareTo(ChecksumStats o) {

        int c;

        c = algorithmName.compareTo(o.algorithmName);

        if (c != 0) {
            return c;
        }

        for (int i = 0; i < binaryChecksum.length; i++) {
            c = Integer.compare(
                Byte.toUnsignedInt(binaryChecksum[i]),
                Byte.toUnsignedInt(o.binaryChecksum[i])
            );
            if (c != 0) {
                return c;
            }
        }

        c = timeStarted.compareTo(o.timeStarted);
        if (c != 0) {
            return c;
        }

        c = timeFinished.compareTo(o.timeFinished);
        if (c != 0) {
            return c;
        }

        return 0;

    }

}
