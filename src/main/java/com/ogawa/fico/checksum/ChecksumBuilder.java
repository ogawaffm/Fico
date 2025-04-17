package com.ogawa.fico.checksum;

import java.security.MessageDigest;

/**
 * Abstract class for building checksums
 *
 * @param <S> the type of the source
 */
public abstract class ChecksumBuilder<S> {

    protected MessageDigest digest;
    private byte[] binaryChecksum;
    private String hexChecksum;
    private boolean finished;

    protected ChecksumBuilder(String algorithmName) {
        init(algorithmName);
    }

    private void init(String algorithmName) {
        try {
            this.digest = MessageDigest.getInstance(algorithmName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.binaryChecksum = new byte[digest.getDigestLength()];
        this.finished = false;
    }

    public void update(S source) {
        finished = false;
    }

    public String getAlgorithmName() {
        return digest.getAlgorithm();
    }

    protected void finish() {
        finishWithChecksum(digest.digest());
        hexChecksum = null;
    }

    protected void finishWithChecksum(byte[] binaryCheckSum) {
        finished = true;
        this.binaryChecksum = binaryCheckSum;
        hexChecksum = null;
        digest.reset();
    }

    public boolean isFinished() {
        return finished;
    }

    /**
     * Finishes the checksum calculation and returns the checksum as a byte-array
     *
     * @return binary checksum
     */
    public byte[] getByteChecksum() {
        if (!finished) {
            finish();
        }
        return binaryChecksum;
    }

    private static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();

    /**
     * Finishes the checksum calculation and returns the checksum as a hex-string
     *
     * @return hex-checksum
     */
    public String getHexChecksum() {

        if (!finished) {
            finish();
        }

        if (hexChecksum == null) {

            hexChecksum = getBytesToHex(binaryChecksum);

        }

        return hexChecksum;

    }

    /**
     * Returns the length in bytes of the checksum
     *
     * @return
     */
    public int getChecksumLength() {
        return digest.getDigestLength();
    }

    /**
     * Returns the hex-string representation of a byte-array
     *
     * @param byteArray
     * @return hex-string
     */
    public static String getBytesToHex(byte[] byteArray) {

        char[] hexChars = new char[byteArray.length * 2];

        for (int index = 0; index < byteArray.length; index++) {

            int byteValue = byteArray[index] & 0xFF;
            hexChars[index * 2] = HEX_CHARS[byteValue >>> 4];
            hexChars[index * 2 + 1] = HEX_CHARS[byteValue & 0x0F];

        }

        return new String(hexChars);

    }

}
