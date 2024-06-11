package com.ogawa.fico.application;

import com.ogawa.fico.checksum.ChecksumBuilder;
import com.ogawa.fico.db.FileRowMapper;
import com.ogawa.fico.db.FileRowUpdater;
import com.ogawa.fico.multithreading.ExtendedExecutorCompletionService;
import com.ogawa.fico.multithreading.ExtendedFutureTask;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.NonNull;

public class ChecksumWriteService implements Runnable {

    private final FileRowUpdater fileRowUpdater;
    private final ThreadPoolExecutor producer;

    private final ExtendedExecutorCompletionService<FileBean> executorCompletionService;

    private final AtomicBoolean isTerminated = new AtomicBoolean(false);

    ChecksumWriteService(@NonNull ExtendedExecutorCompletionService<FileBean> executorCompletionService,
        @NonNull ThreadPoolExecutor producer, @NonNull Connection connection, int scanId) {

        this.producer = producer;
        this.fileRowUpdater = new FileRowUpdater(connection, scanId);
        this.executorCompletionService = executorCompletionService;
    }

    public boolean isTerminated() {
        return isTerminated.get();
    }

    public void run() {

        long fileCount = 0;

        System.out.println(this.getClass().getSimpleName() + " started at " + LocalDateTime.now());
        Future<FileBean> futureTask = null;
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

                System.out.println(
                    callableFileChecksummer.getFileBean().getSize() + " bytes (fileId: " +
                        callableFileChecksummer.getFileBean().getFileId() + ") " +
                        callableFileChecksummer.getFileBean().getFullFileName().toString() + " " +
                        ChecksumBuilder.getBytesToHex(callableFileChecksummer.getFileBean().getChecksum())
                );

                fileRowUpdater.update(callableFileChecksummer.getFileBean());
            }

        } while (!productionEnded || futureTask != null);

        isTerminated.set(true);

        System.out.println(this.getClass().getSimpleName() + " stopped at " + LocalDateTime.now());
    }

}
