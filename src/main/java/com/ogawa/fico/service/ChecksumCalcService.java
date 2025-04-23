package com.ogawa.fico.service;

import static com.ogawa.fico.db.Util.execute;
import static com.ogawa.fico.db.Util.executeIteratively;
import static com.ogawa.fico.db.Util.getSql;

import com.ogawa.fico.checksum.ChecksumBuilder;
import com.ogawa.fico.checksum.FileChecksumBuilder;
import com.ogawa.fico.db.persistence.beanreader.BeanReader;
import com.ogawa.fico.db.persistence.beanreader.SeekingBeanReader;
import com.ogawa.fico.db.persistence.beanwriter.Updater;
import com.ogawa.fico.db.persistence.factory.BeanReaderIterator;
import com.ogawa.fico.db.persistence.factory.FilePersistenceFactory;
import com.ogawa.fico.db.persistence.factory.PersistenceFactory;
import com.ogawa.fico.db.persistence.factory.ScanPersistenceFactory;
import com.ogawa.fico.multithreading.ExtendedExecutorCompletionService;
import com.ogawa.fico.multithreading.ExtendedFutureTask;
import com.ogawa.fico.multithreading.ExtendedThreadFactory;
import com.ogawa.fico.multithreading.ExtendedThreadPoolExecutor;
import com.ogawa.fico.multithreading.ThreadPoolExecutorStatistics;
import com.ogawa.fico.multithreading.ThreadUtils;
import com.ogawa.fico.performance.logging.Formatter;
import com.ogawa.fico.performance.measuring.StopWatch;
import com.ogawa.fico.scan.FileBean;
import com.ogawa.fico.scan.ScanBean;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class ChecksumCalcService {

    private long fileCount = 0;
    private long dirCount = 0;

    final private Connection connection;
    final private CalcMode calcMode;
    final private Long[] scanIds;

    Updater<FileBean> updater;

    public ChecksumCalcService(@NonNull Connection connection, @NonNull CalcMode calcMode, @NonNull Long[] scanIds) {
        this.connection = connection;
        this.calcMode = calcMode;
        // store sorted scanIds to ease logging and debugging
        this.scanIds = Arrays.stream(scanIds).sorted().toArray(Long[]::new);
    }

    private ExtendedThreadPoolExecutor getExecutor() {
        return new ExtendedThreadPoolExecutor(
            ThreadUtils.getCores() * 2, ThreadUtils.getCores() * 3, 0L, TimeUnit.MILLISECONDS,
            //  new PriorityBlockingQueue<ExtendedFutureTask<CallableFileChecksummer>>(),
            new LinkedBlockingQueue<ExtendedFutureTask<CallableFileChecksummer>>(2000),
            new ExtendedThreadFactory("from myFactory", "myThread")
        );
    }

    private void showStatistics(ExtendedThreadPoolExecutor executor, ThreadPoolExecutorStatistics statistics) {
        log.info("Queue size: {}", executor.getQueue().size());
        log.info("Statistics: {}", statistics);
    }

    private void waitUntilAllChecksumsAreCalculated(ExtendedThreadPoolExecutor executor,
        ThreadPoolExecutorStatistics statistics) {

        while (!executor.isTerminated()) {
            try {
                //noinspection BusyWait
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        while (statistics.getStartedCount() > statistics.getFinishedCount()) {
            ThreadUtils.waitSeconds(3);
            showStatistics(executor, statistics);
        }

        showStatistics(executor, statistics);

        executor.unregisterObserver(statistics);

        executor.shutdownNow();

    }

    void logFileBean(FileBean fileBean) {
        if (log.isDebugEnabled()) {
            log.debug("Analyze #{} {} bytes (fileId: {}) {} {})",
                toString(scanIds),
                Formatter.format(fileBean.getSize()),
                Formatter.format(fileBean.getFileId()),
                fileBean.getFullFileName().toString(),
                ChecksumBuilder.getBytesToHex(fileBean.getChecksum())
            );
        } else {
            log.info("{} {}", fileBean.getFullFileName(), ChecksumBuilder.getBytesToHex(fileBean.getChecksum()));
        }
    }

    void write(FileBean fileBean) {
        logFileBean(fileBean);
        updater.update(fileBean);
    }

    private String getSource(ScanBean scanBean) {
        if (FileNaming.isUnc(scanBean.getRoot())) {
            return FileNaming.getUnc(scanBean.getRoot());
        } else {
            return scanBean.getHostName() + ":" + scanBean.getRoot();
        }
    }


    private List<ScanBean> createScanBeans(Connection connection) {
        PersistenceFactory<ScanBean> scanPersistenceFactory = new ScanPersistenceFactory(connection);
        BeanReader<ScanBean> scanReader = scanPersistenceFactory.createTableReader();
        return scanReader.stream().collect(Collectors.toList());
    }

    void createPools(List<ScanBean> scanBeans) {
        Map<String, List<ScanBean>> scansBySource = scanBeans.stream()
            .collect(Collectors.groupingBy(this::getSource));
    }

    long clearChecksums() {
        return execute(connection, getSql(CalcMode.CLEAR.getSqlName()),
            "Analyze #" + toString(scanIds) + " cleared {} checksums in {}",
            new Object[]{scanIds});
    }

    public long calcFileChecksums(Connection connection, CalcMode calcMode, Long[] scanIds) throws SQLException {

        StopWatch totalStopWatch = StopWatch.create();
        totalStopWatch.start();

        ExtendedThreadPoolExecutor executor = getExecutor();

        ThreadPoolExecutorStatistics statistics = new ThreadPoolExecutorStatistics();

        executor.registerObserver(statistics);

        ExtendedExecutorCompletionService<FileBean> executorCompletionService
            = new ExtendedExecutorCompletionService<>(executor);

        PersistenceFactory<FileBean> filePersistenceFactory = new FilePersistenceFactory(connection);
        updater = filePersistenceFactory.createUpdater();

        WriteService<FileBean> writeService = WriteService.create(filePersistenceFactory, executorCompletionService,
            List.of(executor));

        writeService.start();

        SeekingBeanReader<FileBean> beanReader = filePersistenceFactory.createSeekingReader(
            getSql(calcMode.getSqlName()));
        beanReader.seek(new Object[]{scanIds});

        CallableFileChecksummer callableFileChecksummer;

        ExtendedFutureTask<FileBean> futureTask;

        FileBean fileBean;

        long fileCount = 0;

        StopWatch rejectStopWatch = StopWatch.create();
        rejectStopWatch.pause();

        for (Iterator<FileBean> it = new BeanReaderIterator<>(beanReader, true, 0); it.hasNext(); ) {

            fileBean = it.next();

            fileCount++;

            FileChecksumBuilder fileChecksumBuilder = new FileChecksumBuilder(
                "SHA-256", 64 * 1024
            );

            callableFileChecksummer = new CallableFileChecksummer(fileChecksumBuilder, fileBean);

            futureTask = null;

            do {

                try {
                    futureTask = executorCompletionService.submit(callableFileChecksummer);
                    if (rejectStopWatch.isRecording()) {
                        rejectStopWatch.pause();
                        log.debug("Analyze #{} added to calc queue after prior rejection for {}: File #{} {}",
                            toString(scanIds),
                            Formatter.format(rejectStopWatch.getLastRecordedTime()),
                            fileBean.getFileId(), fileBean.getFullFileName()
                        );
                    } else {
                        log.debug("Analyze #{} added to calc queue: File #{} {}",
                            toString(scanIds),
                            fileBean.getFileId(), fileBean.getFullFileName()
                        );
                    }
                } catch (RejectedExecutionException rejectedExecutionException) {
                    if (rejectStopWatch.isPaused()) {
                        rejectStopWatch.resume();
                        log.debug("Analyze #{} Add to calc queue was rejected for the time being for: File #{} {}",
                            toString(scanIds),
                            fileBean.getFileId(), fileBean.getFullFileName()
                        );
                    }
                    ThreadUtils.waitSeconds(1);
                }

            } while (futureTask == null);

            if (fileCount % 100 == 0) {
                showStatistics(executor, statistics);
            }

            if (fileCount == 0) {
                log.info("Analyze #{} No files to calculate checksums for", toString(scanIds));
            }

        }

        rejectStopWatch.stop();
        totalStopWatch.stop();

        if (rejectStopWatch.getRecordingCount() > 0) {
            log.info("Analyze #{} {} files added to queue with a delay in total for a total of {}",
                toString(scanIds),
                Formatter.format(rejectStopWatch.getRecordingCount()),
                Formatter.format(rejectStopWatch.getTotalTime())
            );
        }

        log.debug("Analyze #{} {} files added to queue in {}",
            toString(scanIds),
            fileCount,
            Formatter.format(totalStopWatch.getTotalTime())
        );

        executor.shutdown();
        waitUntilAllChecksumsAreCalculated(executor, statistics);

        if (fileCount % 100 != 0) {
            showStatistics(executor, statistics);
        }

        while (!writeService.isTerminated()) {
            ThreadUtils.waitSeconds(1);
        }

        return fileCount;

    }

    static private final String UPDATE_DIRECTORY_CHECKSUM = "UpdateDirectoryChecksum";

    private static String toString(Long[] array) {
        if (array == null) {
            return "null";
        }
        Long[] sortedIds = Arrays.stream(array).sorted().toArray(Long[]::new);

        StringBuilder sb = new StringBuilder();

        sb.append('[');

        Long pendingId = null;
        Long curId;
        Long lastId = null;

        for (int i = 0; i < sortedIds.length; i++) {

            curId = sortedIds[i];

            // Very first id?
            if (i == 0) {
                sb.append(curId);
            } else {
                // Is there a pending range?
                if (pendingId != null) {
                    // Is this the continuation of a range?
                    if (pendingId == curId - 1) {
                        // yes, update the pendingId
                        pendingId = curId;
                    } else {
                        // no, it's the end of the range
                        sb.append("-");
                        sb.append(pendingId);
                        sb.append(", ");
                        sb.append(curId);
                        pendingId = null;
                    }
                } else {
                    // Is this the beginning of a range?
                    if (lastId == curId - 1) {
                        pendingId = curId;
                    } else {
                        sb.append(", ");
                        sb.append(curId);
                    }
                }
            }

            lastId = curId;
        }

        if (pendingId != null) {
            sb.append("-");
            sb.append(pendingId);
        }

        sb.append(']');

        return sb.toString();
    }

    private long calcDirChecksums() {

        long totalUpdatedDirCount;

        log.info("Calculating directory checksums...");

        totalUpdatedDirCount = executeIteratively(connection, getSql(UPDATE_DIRECTORY_CHECKSUM),
            "Analyze #" + toString(scanIds) + " step #{} updated {} directories in {}",
            "Analyze #" + toString(scanIds) + " step #{} (final check step) took {}",
            "Analyze #" + toString(scanIds) + " updated {} directories in {} steps took {}",
            new Object[]{scanIds});

        log.info("Analyze #{} finished directory checksum calculation", toString(scanIds));

        return totalUpdatedDirCount;

    }

    public void calc() throws SQLException {

        if (calcMode == CalcMode.CLEAR) {
            fileCount = clearChecksums();
        } else {
            fileCount = calcFileChecksums(connection, calcMode, scanIds);

            log.info("Analyze #{} calculated {} file checksums", toString(scanIds), fileCount);

            if (fileCount == 0) {
                log.info("Analyze #{} skipped calculation of directory checksums, because of no files",
                    toString(scanIds));
            } else {
                dirCount = calcDirChecksums();
            }
        }

    }

}
