package com.ogawa.ficoold.checksum;

import java.time.Duration;
import java.time.LocalDateTime;
import lombok.NonNull;

public class ChecksumStats implements Comparable<ChecksumStats> {

    private ChecksumBuilder checksumBuilder;
    private String algorithmName;
    private byte[] binaryChecksum;
    private LocalDateTime timeStarted;
    private LocalDateTime timeFinished;

    public ChecksumStats(@NonNull ChecksumBuilder checksumBuilder) {
        this.checksumBuilder = checksumBuilder;
    }

    /**
     * Returns the algorithm name. If finished the stored name is returned else this of the still active checksum
     * builder
     *
     * @return
     */

    public String getAlgorithmName() {
        return isFinished() ? algorithmName : this.checksumBuilder.getAlgorithmName();
    }

    public byte[] getBinaryChecksum() {
        return binaryChecksum;
    }

    public String getHexChecksum() {
        return ChecksumBuilder.getBytesToHex(binaryChecksum);
    }

    public void start() {
        timeStarted = LocalDateTime.now();
    }

    public void finish() {
        timeFinished = LocalDateTime.now();
        binaryChecksum = checksumBuilder.getBinaryChecksum();
        algorithmName = checksumBuilder.getAlgorithmName();

        // release reference to the checksum builder
        checksumBuilder = null;
    }

    public boolean isFinished() {
        return timeFinished == null;
    }

    public LocalDateTime getTimeStarted() {
        return timeStarted;
    }

    public LocalDateTime getTimeFinished() {
        return timeFinished;
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
