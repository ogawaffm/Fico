package com.ogawa.fico.scan;

import com.ogawa.fico.db.Sequence;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.NonNull;

public class FileBeanFactory {

    private final long scanId;

    private final Sequence fileIdSequence;

    public FileBeanFactory(long scanId, Sequence fileIdSequence) {
        this.scanId = scanId;
        this.fileIdSequence = fileIdSequence;
    }

    static public FileBean create(Long fileId, Long dirId, long scanId, Path filename, Long size,
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

    public FileBean create(Long parentDirId, long scanId,
        @NonNull Path filename,
        @NonNull BasicFileAttributes attributes) {

        return create(
            fileIdSequence.next(),
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
