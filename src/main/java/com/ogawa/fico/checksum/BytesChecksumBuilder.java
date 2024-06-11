package com.ogawa.fico.checksum;

public class BytesChecksumBuilder extends ChecksumBuilder<byte[]> {

    public BytesChecksumBuilder(String algorithmName) {
        super(algorithmName);
    }

    @Override
    public void update(byte[] bytes) {
        super.update(bytes);
        digest.update(bytes, 0, bytes.length);
    }

}
