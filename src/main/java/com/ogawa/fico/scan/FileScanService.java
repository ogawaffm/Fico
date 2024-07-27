package com.ogawa.fico.scan;

import com.ogawa.fico.multithreading.ExtendedFutureTask;
import com.ogawa.fico.multithreading.ExtendedThreadFactory;
import com.ogawa.fico.multithreading.ExtendedThreadPoolExecutor;
import com.ogawa.fico.multithreading.ThreadUtils;
import java.nio.file.Path;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileScanService extends FileScanner {


    private final Collection<Path> rootPaths;

    public FileScanService(Collection<Path> rootPaths, String databaseName) {
        super(databaseName);
        this.rootPaths = rootPaths;
    }

    public Long call() {

        ExtendedThreadPoolExecutor executor = getExecutor();

        for (Path rootPath : rootPaths) {
            SimpleFileScanner fileScanner = new SimpleFileScanner(rootPath, getDatabaseName());
            executor.submit(new ExtendedFutureTask<>(fileScanner));
        }

        executor.shutdown();

        boolean finished = false;

        while (!finished) {
            try {
                finished = executor.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("Interrupted while waiting for executor to terminate", e);
                finished = true;
            }
        }

        return -1L;

    }

    private ExtendedThreadPoolExecutor getExecutor() {
        return new ExtendedThreadPoolExecutor(
            ThreadUtils.getCores() * 2, ThreadUtils.getCores() * 3, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<ExtendedFutureTask<FileScanner>>(rootPaths.size()),
            new ExtendedThreadFactory(FileScanner.class.getSimpleName() + "-pool", FileScanner.class.getSimpleName())
        );
    }


}
