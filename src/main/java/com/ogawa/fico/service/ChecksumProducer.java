package com.ogawa.fico.service;

import com.ogawa.fico.multithreading.ExtendedExecutorCompletionService;
import com.ogawa.fico.multithreading.ExtendedFutureTask;
import com.ogawa.fico.multithreading.ExtendedThreadFactory;
import com.ogawa.fico.multithreading.ExtendedThreadPoolExecutor;
import com.ogawa.fico.multithreading.ThreadPoolExecutorStatistics;
import com.ogawa.fico.multithreading.ThreadUtils;
import com.ogawa.fico.scan.FileBean;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ChecksumProducer {

    private final ExtendedThreadPoolExecutor executor;

    ChecksumProducer() {
        executor = createExecutor();

        ThreadPoolExecutorStatistics statistics = new ThreadPoolExecutorStatistics();

        executor.registerObserver(statistics);

        ExtendedExecutorCompletionService<FileBean> executorCompletionService
            = new ExtendedExecutorCompletionService<>(executor);

    }

    private ExtendedThreadPoolExecutor createExecutor() {
        return new ExtendedThreadPoolExecutor(
            ThreadUtils.getCores() * 2, ThreadUtils.getCores() * 3, 0L, TimeUnit.MILLISECONDS,
            //  new PriorityBlockingQueue<ExtendedFutureTask<CallableFileChecksummer>>(),
            new LinkedBlockingQueue<ExtendedFutureTask<CallableFileChecksummer>>(2000),
            new ExtendedThreadFactory("from myFactory", "myThread")
        );
    }


}
