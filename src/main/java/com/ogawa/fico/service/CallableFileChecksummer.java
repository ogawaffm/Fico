package com.ogawa.fico.service;

import com.ogawa.fico.checksum.FileChecksumBuilder;
import com.ogawa.fico.scan.FileBean;
import java.time.LocalDateTime;
import java.util.Comparator;
import lombok.NonNull;

public class CallableFileChecksummer implements BeanEncapsulatingCallable<FileBean>,
    Comparator<CallableFileChecksummer> {

    private final FileBean fileBean;
    private final FileChecksumBuilder fileChecksumBuilder;

    public CallableFileChecksummer(@NonNull FileChecksumBuilder fileChecksumBuilder, @NonNull FileBean fileBean) {
        this.fileChecksumBuilder = fileChecksumBuilder;
        this.fileBean = fileBean;
    }

    public FileBean call() {
        fileBean.setCalcStarted(LocalDateTime.now());
        fileChecksumBuilder.update(fileBean.getFullFileName());
        fileBean.setChecksum(fileChecksumBuilder.getByteChecksum());
        fileBean.setCalcFinished(LocalDateTime.now());
        return fileBean;
    }

    @Override
    public int compare(CallableFileChecksummer o1, CallableFileChecksummer o2) {
        return fileBean.compareTo(o1.getBean());
    }

    @Override
    public FileBean getBean() {
        return fileBean;
    }

}
