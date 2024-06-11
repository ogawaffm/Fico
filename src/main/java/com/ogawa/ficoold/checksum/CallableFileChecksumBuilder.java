package com.ogawa.ficoold.checksum;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PUBLIC)
public class CallableFileChecksumBuilder<R extends CallableFileChecksumBuilder> extends ChecksumBuilder<Path>
    implements Callable<R> {

    final private byte[] buffer;

    final private Path path;
    final private long fileSize;

    public CallableFileChecksumBuilder(String algorithmName, int fileBufferSize, Path path) {
        super(algorithmName);
        this.buffer = new byte[fileBufferSize];
        this.path = path;
        try {
            this.fileSize = Files.size(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ChecksumBuilder<Path> update(Path path) {

        try (FileInputStream fis = new FileInputStream(path.toFile())) {

            int byteCountRead;

            while ((byteCountRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, byteCountRead);
            }

        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }

        return this;

    }

    public R call() {
        update(path);
        return (R) this;
    }

}
