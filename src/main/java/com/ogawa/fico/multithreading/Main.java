package com.ogawa.fico.multithreading;

import com.ogawa.ficoold.checksum.CallableFileChecksumBuilder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;

public class Main {

    private static void showStatistics(ExtendedThreadPoolExecutor executor, ThreadPoolExecutorStatistics statistics) {
        System.out.println("Queue size:" + executor.getQueue().size());
        System.out.println("Statistics:" + statistics);
    }

    static private Runnable createRunnable() {
        return new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                Thread.sleep(1000L);
                System.out.println("Hello from thread " + Thread.currentThread().getName());
            }
        };
    }

    static private <V> Callable<V> createCallable(V valueToReturn) {
        return Executors.callable(createRunnable(), valueToReturn);
    }

    @SneakyThrows
    public static void main(String[] args) {

        Comparator<CallableFileChecksumBuilder> fileSizeComparator
            = Comparator.comparing(CallableFileChecksumBuilder::getFileSize);
        Comparator<CallableFileChecksumBuilder> reversedFileSizeComparator =
            fileSizeComparator.reversed().thenComparing(CallableFileChecksumBuilder::getPath);

        ExtendedThreadPoolExecutor executor = new ExtendedThreadPoolExecutor(
            ThreadUtils.getCores(), ThreadUtils.getCores() * 3, 0L, TimeUnit.MILLISECONDS,
            new PriorityBlockingQueue<CallableFileChecksumBuilder>(11, reversedFileSizeComparator),
            new ExtendedThreadFactory("from myFactory", "myThread")
        );

        ThreadPoolExecutorStatistics statistics = new ThreadPoolExecutorStatistics();

        executor.registerObserver(statistics);

        List<Callable<Integer>> list = new ArrayList<>();
        list.add(createCallable(1));
        list.add(createCallable(2));
        list.add(createCallable(3));

        executor.invokeAll(list);

        for (int i = 0; i < executor.getCorePoolSize() * 3; i++) {
            Thread.sleep(100L);
            ExtendedFutureTask<?> futureTask = new ExtendedFutureTask<>(createRunnable());

            executor.submit(futureTask);
            showStatistics(executor, statistics);

        }

        while (statistics.getStartedCount() > statistics.getFinishedCount()) {
            ThreadUtils.waitSeconds(1);
            showStatistics(executor, statistics);
        }

        showStatistics(executor, statistics);

        executor.unregisterObserver(statistics);

        executor.shutdownNow();

    }

}
