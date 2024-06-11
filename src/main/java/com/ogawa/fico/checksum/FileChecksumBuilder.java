package com.ogawa.fico.checksum;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

public class FileChecksumBuilder extends ChecksumBuilder<Path> {

    private final byte[] buffer;

    public FileChecksumBuilder(String algorithmName, int fileBufferSize) {
        super(algorithmName);
        this.buffer = new byte[fileBufferSize];
    }

    @Override
    public void update(Path path) {

        super.update(path);

        try (FileInputStream fis = new FileInputStream(path.toFile())) {

            int byteCountRead;

            while ((byteCountRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, byteCountRead);
            }

        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }

    }

}
