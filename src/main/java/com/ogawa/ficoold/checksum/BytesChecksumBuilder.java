package com.ogawa.ficoold.checksum;

public class BytesChecksumBuilder extends ChecksumBuilder<byte[]> {

  public BytesChecksumBuilder(String algorithmName) {
    super(algorithmName);
  }

  public BytesChecksumBuilder update(byte[] bytes) {
    super.update(bytes);
    digest.update(bytes, 0, bytes.length);
    return this;
  }

}
