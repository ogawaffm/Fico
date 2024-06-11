package com.ogawa.fico.application;


import static com.ogawa.fico.db.Util.SELECT_ASTERISK_MARKED_DUPLICATE_CANDIDATES;
import static java.util.Comparator.naturalOrder;

import com.ogawa.fico.checksum.FileChecksumBuilder;
import com.ogawa.fico.multithreading.ExtendedExecutorCompletionService;
import com.ogawa.fico.multithreading.ExtendedFutureTask;
import com.ogawa.fico.multithreading.ExtendedThreadFactory;
import com.ogawa.fico.multithreading.ExtendedThreadPoolExecutor;
import com.ogawa.fico.multithreading.ThreadPoolExecutorStatistics;
import com.ogawa.fico.multithreading.ThreadUtils;
import com.ogawa.fico.db.Util;
import java.sql.Connection;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class ChecksumCalcService {

    private void markDuplicateCandidates(Connection connection) {
        Date start = new Date();
        System.out.println("Marking duplicate candidates...");
        int markedRowCount = Util.createDuplicateCandidates(connection);
        System.out.println("markedRowCount: " + markedRowCount + " in "
            + (new Date().getTime() - start.getTime()) / 1000 + " s");
    }

    private Comparator<FileBean> getFileBeanPriorityComparator() {
        Comparator<FileBean> fileSizeComparator
            = Comparator.comparing(FileBean::getSize);

        Comparator<FileBean> reversedFileSizeAndPathComparator =
            fileSizeComparator.reversed().thenComparing(FileBean::getFullFileName);

        return reversedFileSizeAndPathComparator;
    }

    private Comparator<ExtendedFutureTask<CallableFileChecksummer>> getFutureTaskPriorityComparator() {
        return new Comparator<ExtendedFutureTask<CallableFileChecksummer>>() {
            @Override
            public int compare(
                ExtendedFutureTask<CallableFileChecksummer> o1,
                ExtendedFutureTask<CallableFileChecksummer> o2) {
                return getFileBeanPriorityComparator().compare(
                    ((CallableFileChecksummer) o1.getTask()).getFileBean(),
                    ((CallableFileChecksummer) o2.getTask()).getFileBean()
                );
            }
        };
    }

    private ExtendedThreadPoolExecutor getExecutor() {
        return new ExtendedThreadPoolExecutor(
            ThreadUtils.getCores() * 2, ThreadUtils.getCores() * 3, 0L, TimeUnit.MILLISECONDS,
            //  new PriorityBlockingQueue<ExtendedFutureTask<CallableFileChecksummer>>(),
            new LinkedBlockingQueue<ExtendedFutureTask<CallableFileChecksummer>>(2000),
            new ExtendedThreadFactory("from myFactory", "myThread")
        );
//        return new ExtendedThreadPoolExecutor(
//            ThreadUtils.getCores(), ThreadUtils.getCores() * 3, 0L, TimeUnit.MILLISECONDS,
//            new PriorityBlockingQueue<ExtendedFutureTask<CallableFileChecksummer>>(11,
//                getFutureTaskPriorityComparator()),
//            new ExtendedThreadFactory("from myFactory", "myThread")
//        );
    }

    private void showStatistics(ExtendedThreadPoolExecutor executor, ThreadPoolExecutorStatistics statistics) {
        System.out.println("Queue size:" + executor.getQueue().size());
        System.out.println("Statistics:" + statistics);
    }

    private void waitUntilAllChecksumsAreCalculated(ExtendedThreadPoolExecutor executor,
        ThreadPoolExecutorStatistics statistics) {

        while (!executor.isTerminated()) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        while (statistics.getStartedCount() > statistics.getFinishedCount()) {
            ThreadUtils.waitSeconds(1);
            showStatistics(executor, statistics);
        }

        showStatistics(executor, statistics);

        executor.unregisterObserver(statistics);

        executor.shutdownNow();

    }

    public long sum(Connection connection, int scanId) {

        markDuplicateCandidates(connection);

        ExtendedThreadPoolExecutor executor = getExecutor();

        ThreadPoolExecutorStatistics statistics = new ThreadPoolExecutorStatistics();

        executor.registerObserver(statistics);

        ExtendedExecutorCompletionService<FileBean> executorCompletionService
            = new ExtendedExecutorCompletionService<>(executor);

        ChecksumWriteService checksumWriteService = new ChecksumWriteService(executorCompletionService, executor,
            connection, scanId);

        new Thread(checksumWriteService).start();

        FileBeanProvider fileBeanProvider = new FileBeanProvider(connection,
            SELECT_ASTERISK_MARKED_DUPLICATE_CANDIDATES);

        //CallableFileChecksummer callableFileChecksummer;
        Callable<FileBean> callableFileChecksummer;

        ExtendedFutureTask<FileBean> futureTask = null;

        long fileCount = 0;
        long rejectionCount = 0;

        for (FileBeanProvider it = fileBeanProvider; it.hasNext(); ) {

            FileBean fileBean = it.next();

            fileCount++;

            FileChecksumBuilder fileChecksumBuilder = new FileChecksumBuilder(
                "SHA-256", 64 * 1024
            );

            callableFileChecksummer = new CallableFileChecksummer(fileChecksumBuilder, fileBean);

            do {

                try {
                    futureTask = executorCompletionService.submit(callableFileChecksummer);
                } catch (RejectedExecutionException rejectedExecutionException) {
                    System.out.println("RejectedExecutionException: " + ++rejectionCount);
                    ThreadUtils.waitSeconds(1);
                }

            } while (futureTask == null);

            if (fileCount % 100 == 0) {
                showStatistics(executor, statistics);
            }
        }

        executor.shutdown();
        // waitUntilAllChecksumsAreCalculated(executor, statistics);

        if (fileCount % 100 != 0) {
            showStatistics(executor, statistics);
        }

        while (!checksumWriteService.isTerminated()) {
            ThreadUtils.waitSeconds(1);
        }

        return fileCount;

    }

}
