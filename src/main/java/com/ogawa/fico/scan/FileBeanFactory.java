package com.ogawa.fico.scan;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.NonNull;

public class FileBeanFactory {

    static public FileBean create(Long fileId, Long dirId, Long scanId, Path filename, Long size,
        boolean isDirectory, Integer filesContained, Integer dirsContained,
        LocalDateTime modificationTime, LocalDateTime creationTime,
        byte[] checksum, LocalDateTime calcStarted, LocalDateTime calcFinished) {

        FileBean fileBean = new FileBean();
        fileBean.fileId = fileId;
        fileBean.dirId = dirId;
        fileBean.scanId = scanId;
        fileBean.fullFileName = filename;
        fileBean.size = size;
        fileBean.isDirectory = isDirectory;
        fileBean.filesContained = filesContained;
        fileBean.dirsContained = dirsContained;
        fileBean.modificationTime = modificationTime;
        fileBean.creationTime = creationTime;
        fileBean.checksum = checksum;
        fileBean.calcStarted = calcStarted;
        fileBean.calcFinished = calcFinished;

        return fileBean;
    }

    static public FileBean create(Long parentDirId, long scanId,
        @NonNull Path filename,
        @NonNull BasicFileAttributes attributes) {

        return create(
            null, //fileIdSequence.next(),
            parentDirId,
            scanId,
            filename,
            attributes.isDirectory() ? null : attributes.size(),
            attributes.isDirectory(),
            null,
            null,
            attributes.lastModifiedTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
            attributes.creationTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
            null,
            null,
            null
        );

    }

}
