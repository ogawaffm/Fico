package com.ogawa.fico.application;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.NonNull;

public class FileBeanFactory {

    public static FileBean create(Long fileId, Long dirId, long scanId, Path filename, long size,
        LocalDateTime lastWriteAccess, byte[] checksum, LocalDateTime calcStarted, LocalDateTime calcFinished) {

        FileBean fileBean = new FileBean();
        fileBean.fileId = fileId;
        fileBean.dirId = dirId;
        fileBean.scanId = scanId;
        fileBean.fullFileName = filename;
        fileBean.size = size;
        fileBean.lastWriteAccess = lastWriteAccess;
        fileBean.checksum = checksum;
        fileBean.calcStarted = calcStarted;
        fileBean.calcFinished = calcFinished;

        return fileBean;
    }

    public static FileBean create(Long parentDirId, int scanId,
        @NonNull Path filename,
        @NonNull BasicFileAttributes attributes) {

        return create(
            null,
            parentDirId,
            scanId,
            filename,
            attributes.isDirectory() ? -1 : attributes.size(),
            attributes.lastModifiedTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
            null,
            null,
            null
        );

    }

    public static String dirOfPath(Path path) {
        return path.getParent().toString();
    }

    public static String nameOfPath(Path path) {
        return path.getFileName().toString();
    }

}
