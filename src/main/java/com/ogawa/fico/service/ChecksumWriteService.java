package com.ogawa.fico.service;

import com.ogawa.fico.checksum.ChecksumBuilder;
import com.ogawa.fico.db.persistence.beanwriter.Updater;
import com.ogawa.fico.db.persistence.beanwriter.Creator;
import com.ogawa.fico.db.persistence.factory.FilePersistenceFactory;
import com.ogawa.fico.multithreading.ExtendedExecutorCompletionService;
import com.ogawa.fico.multithreading.ExtendedFutureTask;
import com.ogawa.fico.performance.logging.Formatter;
import com.ogawa.fico.scan.FileBean;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChecksumWriteService<B> implements Runnable {

    private final Creator creator;
    private final Updater fileBeanUpdater;
    private final ThreadPoolExecutor producer;

    private final ExtendedExecutorCompletionService<FileBean> executorCompletionService;

    private final AtomicBoolean isTerminated = new AtomicBoolean(false);

    ChecksumWriteService(@NonNull ExtendedExecutorCompletionService<FileBean> executorCompletionService,
        @NonNull ThreadPoolExecutor producer, @NonNull Connection connection) throws SQLException {

        this.producer = producer;
        fileBeanUpdater = new FilePersistenceFactory(connection).createUpdater();
        creator = new FilePersistenceFactory(connection).createCreator();
        this.executorCompletionService = executorCompletionService;
    }

    public boolean isTerminated() {
        return isTerminated.get();
    }

    private String getServiceName() {
        return this.getClass().getSimpleName();
    }

    void logFileBean(FileBean fileBean) {
        if (log.isDebugEnabled()) {
            log.debug("{} bytes (fileId: {}) {} {})",
                Formatter.format(fileBean.getSize()),
                Formatter.format(fileBean.getFileId()),
                fileBean.getFullFileName().toString(),
                ChecksumBuilder.getBytesToHex(fileBean.getChecksum())
            );
        } else {
            log.info("{} {}", fileBean.getFullFileName(), ChecksumBuilder.getBytesToHex(fileBean.getChecksum()));
        }
    }

    public void run() {

        log.info("{} started", getServiceName());
        Future<FileBean> futureTask;
        CallableFileChecksummer callableFileChecksummer;

        boolean productionEnded;

        do {

            // determine if production has ended first, then poll to have a delay to place the last task
            productionEnded = producer.isTerminated();

            try {
                futureTask = executorCompletionService.poll(500, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ignore) {
                log.error("{} was interrupted", getServiceName());
                futureTask = null;
            }

            if (futureTask != null) {

                callableFileChecksummer = (CallableFileChecksummer) ((ExtendedFutureTask<FileBean>) futureTask).getTask();

                FileBean fileBean = callableFileChecksummer.getBean();

                logFileBean(fileBean);

                fileBeanUpdater.update(fileBean);
            }

        } while (!productionEnded || futureTask != null);

        try {
            fileBeanUpdater.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        isTerminated.set(true);

        log.info("{} stopped", getServiceName());
    }

}
