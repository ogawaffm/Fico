package com.ogawa.fico.application;

import com.ogawa.fico.checksum.FileChecksumBuilder;
import java.util.Comparator;
import java.util.concurrent.Callable;
import lombok.Getter;
import lombok.NonNull;

public class CallableFileChecksummer implements Callable<FileBean>, Comparator<CallableFileChecksummer> {

    @Getter
    private final FileBean fileBean;
    private final FileChecksumBuilder fileChecksumBuilder;

    public CallableFileChecksummer(@NonNull FileChecksumBuilder fileChecksumBuilder, @NonNull FileBean fileBean) {
        this.fileChecksumBuilder = fileChecksumBuilder;
        this.fileBean = fileBean;
    }

    public FileBean call() {
        fileChecksumBuilder.update(fileBean.getFullFileName());
        fileBean.setChecksum(fileChecksumBuilder.getByteChecksum());
        return fileBean;
    }

    @Override
    public int compare(CallableFileChecksummer o1, CallableFileChecksummer o2) {
        return fileBean.compareTo(o1.getFileBean());
    }
}
