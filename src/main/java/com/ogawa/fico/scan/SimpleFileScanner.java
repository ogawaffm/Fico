package com.ogawa.fico.scan;

import static com.ogawa.fico.db.Util.execute;
import static com.ogawa.fico.db.Util.executeIteratively;
import static com.ogawa.fico.db.Util.getSql;

import com.ogawa.fico.PersistingFileVisitor;
import com.ogawa.fico.db.FileIdSequenceFactory;
import com.ogawa.fico.db.ScanRowWriter;
import com.ogawa.fico.db.Sequence;
import com.ogawa.fico.db.persistence.beanwriter.Creator;
import com.ogawa.fico.db.persistence.beanwriter.Updater;
import com.ogawa.fico.db.persistence.factory.FilePersistenceFactory;
import com.ogawa.fico.db.persistence.factory.ScanPersistenceFactory;
import com.ogawa.fico.performance.logging.Formatter;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
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

    private long updateDirectories() {
        long totalUpdatedDirCount;

        log.info("Calculating directory size and content...");

        totalUpdatedDirCount = execute(getConnection(), getSql(UPDATE_EMPTY_DIRECTORY_SIZE_AND_CONTENT),
            "empty directories");

        try {
            getConnection().commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        totalUpdatedDirCount =
            totalUpdatedDirCount + executeIteratively(getConnection(), getSql(UPDATE_DIRECTORY_SIZE_AND_CONTENT),
                "directories");

        log.info("Finished calculating directory size and content");

        return totalUpdatedDirCount;
    }

    @Override
    public Long call() {

        Path absoluteRootPath = rootPath.toAbsolutePath();

        ScanRowWriter scanRowWriter = new ScanRowWriter(getConnection());

        long scanId = scanRowWriter.create(rootPath);

        // Not an absolute path?
        if (!rootPath.equals(absoluteRootPath)) {
            log.debug("Scan #{} resolved path to add from {} to {}", scanId, rootPath, absoluteRootPath);
        }

        Connection connection = getConnection();

        ScanPersistenceFactory scanPersistenceFactory = new ScanPersistenceFactory(getConnection());
        Creator scanCreator = scanPersistenceFactory.createCreator();
        Updater scanUpdater = scanPersistenceFactory.createUpdater();

        Sequence fileIdSequence = FileIdSequenceFactory.getFileIdSequence(connection, scanId);

        Creator fileCreator = new FilePersistenceFactory(connection).createCreator();

        PersistingFileVisitor fileVisitor = new PersistingFileVisitor(
            scanId, absoluteRootPath, fileIdSequence, fileCreator
        );

        scanRowWriter.updateStarted(scanId, new Date());

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
        scanRowWriter.updateFinished(scanId, new Date());

        log.info("Scan #{} added {} files from {} directories and total size of {} bytes to {} database",
            scanId,
            Formatter.format((long) fileVisitor.getFileCount()),
            Formatter.format((long) fileVisitor.getDirCount()),
            Formatter.format((long) fileVisitor.getTotalSize()),
            getDatabaseName()
        );

        updateDirectories();

        return fileVisitor.getFileCount();

    }
}
