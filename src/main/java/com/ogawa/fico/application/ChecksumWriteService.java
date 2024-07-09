package com.ogawa.fico.application;

import com.ogawa.fico.checksum.ChecksumBuilder;
import com.ogawa.fico.db.FileRowUpdater;
import com.ogawa.fico.multithreading.ExtendedExecutorCompletionService;
import com.ogawa.fico.multithreading.ExtendedFutureTask;
import com.ogawa.fico.performance.logging.Formatter;
import java.sql.Connection;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChecksumWriteService implements Runnable {

    private final FileRowUpdater fileRowUpdater;
    private final ThreadPoolExecutor producer;

    private final ExtendedExecutorCompletionService<FileBean> executorCompletionService;

    private final AtomicBoolean isTerminated = new AtomicBoolean(false);

    ChecksumWriteService(@NonNull ExtendedExecutorCompletionService<FileBean> executorCompletionService,
        @NonNull ThreadPoolExecutor producer, @NonNull Connection connection) {

        this.producer = producer;
        this.fileRowUpdater = new FileRowUpdater(connection);
        this.executorCompletionService = executorCompletionService;
    }

    public boolean isTerminated() {
        return isTerminated.get();
    }

    void printFileBean(FileBean fileBean) {
        if (log.isDebugEnabled()) {
            log.debug(
                Formatter.format(fileBean.getSize())
                    + " bytes (fileId: " + Formatter.format(fileBean.getFileId()) + ") "
                    + fileBean.getFullFileName().toString()
                    + " " + ChecksumBuilder.getBytesToHex(fileBean.getChecksum())
            );
        } else {
            log.info(
                fileBean.getFullFileName().toString() + " " + ChecksumBuilder.getBytesToHex(fileBean.getChecksum()));
        }
    }

    public void run() {

        log.info(this.getClass().getSimpleName() + " started");
        Future<FileBean> futureTask;
        CallableFileChecksummer callableFileChecksummer;

        boolean productionEnded;

        do {

            // determine if production has ended first, then poll to have a delay to place the last task
            productionEnded = producer.isTerminated();

            try {
                futureTask = executorCompletionService.poll(500, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ignore) {
                futureTask = null;
            }

            if (futureTask != null) {

                callableFileChecksummer = (CallableFileChecksummer) ((ExtendedFutureTask<FileBean>) futureTask).getTask();

                FileBean fileBean = callableFileChecksummer.getFileBean();

                printFileBean(fileBean);

                fileRowUpdater.update(fileBean);
            }

        } while (!productionEnded || futureTask != null);

        fileRowUpdater.close();

        isTerminated.set(true);

        log.info(this.getClass().getSimpleName() + " stopped");
    }

}
