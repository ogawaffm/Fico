package com.ogawa.fico.scan;

import static com.ogawa.fico.db.Util.execute;
import static com.ogawa.fico.db.Util.executeIteratively;
import static com.ogawa.fico.db.Util.getSql;

import com.ogawa.fico.PersistingFileVisitor;
import com.ogawa.fico.db.persistence.beanwriter.Creator;
import com.ogawa.fico.db.persistence.beanwriter.Updater;
import com.ogawa.fico.db.persistence.factory.FilePersistenceFactory;
import com.ogawa.fico.db.persistence.factory.PersistenceFactory;
import com.ogawa.fico.db.persistence.factory.ScanPersistenceFactory;
import com.ogawa.fico.performance.logging.Formatter;
import com.ogawa.fico.performance.measuring.StopWatch;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleFileScanner extends FileScanner {

    static private final String UPDATE_DIRECTORY_SIZE_AND_CONTENT = "UpdateDirectorySizeAndContent";
    static private final String UPDATE_EMPTY_DIRECTORY_SIZE_AND_CONTENT = "UpdateEmptyDirectorySizeAndContent";

    private final Path rootPath;

    public SimpleFileScanner(Path rootPath, String databaseName) {
        super(databaseName);
        this.rootPath = rootPath;
    }

    private long updateDirectories(Long scanId) {

        StopWatch stopWatch = StopWatch.create();
        stopWatch.start();

        long totalUpdatedDirCount;

        log.info("Scan #{} Calculating directory size and content...", scanId);

        totalUpdatedDirCount = execute(getConnection(), getSql(UPDATE_EMPTY_DIRECTORY_SIZE_AND_CONTENT),
            "Scan #" + scanId + " updated {} empty directories in {}", scanId);

        try {
            getConnection().commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        totalUpdatedDirCount =
            totalUpdatedDirCount + executeIteratively(getConnection(), getSql(UPDATE_DIRECTORY_SIZE_AND_CONTENT),
                "Scan #" + scanId + " step #{} updated {} directories in {}",
                "Scan #" + scanId + " step #{} (final check step) took {}",
                "Scan #" + scanId + " updated {} directories in {} steps in {}",
                scanId);

        stopWatch.stop();
        log.info("Scan #{} finished calculating of {} directory size and content in {}",
            scanId,
            totalUpdatedDirCount,
            Formatter.format(stopWatch.getTotalTime()));

        return totalUpdatedDirCount;
    }

    @Override
    public Long call() {

        Path absoluteRootPath = rootPath.toAbsolutePath();

//        ScanRowWriter scanRowWriter = new ScanRowWriter(getConnection());

//        long scanId = scanRowWriter.create(rootPath);

        Connection connection = getConnection();

        PersistenceFactory scanPersistenceFactory = new ScanPersistenceFactory(getConnection());
        Creator scanCreator = scanPersistenceFactory.createCreator(1);
        Updater scanUpdater = scanPersistenceFactory.createUpdater();

        ScanBean scan = ScanBeanFactory.create(absoluteRootPath.toString());
        scan.setStarted(LocalDateTime.now());

        // persist scan to database to get scanId
        scanCreator.create(scan);

        // Not an absolute path?
        if (!rootPath.equals(absoluteRootPath)) {
            log.debug("Scan #{} resolved path to add from {} to {}", scan.getScanId(), rootPath, absoluteRootPath);
        }

        // create an unbatched creator for directories to immediately persist them to retrieve dirId
        Creator dirCreator = new FilePersistenceFactory(connection).createCreator(0);
        Creator fileCreator = new FilePersistenceFactory(connection).createCreator();

        PersistingFileVisitor fileVisitor = new PersistingFileVisitor(
            scan.getScanId(), absoluteRootPath, dirCreator, fileCreator
        );

        try {
            fileVisitor.walk();
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }

        try {
            fileCreator.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        log.info("Scan #{} added {} files from {} directories and total size of {} bytes to {} database",
            scan.getScanId(),
            Formatter.format((long) fileVisitor.getFileCount()),
            Formatter.format((long) fileVisitor.getDirCount()),
            Formatter.format((long) fileVisitor.getTotalSize()),
            getDatabaseName()
        );

        updateDirectories(scan.getScanId());

        scan.setFinished(LocalDateTime.now());
        scanUpdater.update(scan);
        try {
            scanUpdater.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return fileVisitor.getFileCount();

    }
}
