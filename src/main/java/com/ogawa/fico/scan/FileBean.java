package com.ogawa.fico.scan;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Comparator;
import lombok.Data;

@Data
public class FileBean implements Comparable<FileBean> {

    Long fileId;
    Long dirId;
    Long scanId;
    Path fullFileName;
    Long size;
    Boolean isDirectory;
    Integer filesContained;
    Integer dirsContained;
    LocalDateTime modificationTime;
    LocalDateTime creationTime;
    byte[] checksum;
    LocalDateTime calcStarted;
    LocalDateTime calcFinished;

    @Override
    public int compareTo(FileBean o) {
        return fileBeanPriorityComparator.compare(this, o);
    }

    private static Comparator<FileBean> fileBeanPriorityComparator = getFileBeanPriorityComparator();

    private static Comparator<FileBean> getFileBeanPriorityComparator() {
        Comparator<FileBean> fileSizeComparator = Comparator.comparing(FileBean::getSize);

        Comparator<FileBean> reversedFileSizeAndPathComparator =
            fileSizeComparator.reversed().thenComparing(FileBean::getFullFileName);

        return reversedFileSizeAndPathComparator;
    }

}
