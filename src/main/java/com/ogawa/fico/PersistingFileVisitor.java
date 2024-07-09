package com.ogawa.fico;

import com.ogawa.fico.application.FileBean;
import com.ogawa.fico.application.FileBeanFactory;
import com.ogawa.fico.db.FileRowCreator;
import com.ogawa.fico.db.Sequence;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import java.util.Stack;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PersistingFileVisitor implements FileVisitor<Path> {

    private final Path path;

    private final FileBeanFactory fileBeanFactory;
    private final FileRowCreator fileRowCreator;
    private final long scanId;

    private Long dirId = null;

    final private Stack<Long> dirIdStack = new Stack<>();

    @Getter
    private long fileCount = 0;
    @Getter
    private long dirCount = 0;

    public PersistingFileVisitor(long scanId, Path path, Sequence fileIdSequence, FileRowCreator fileRowCreator) {

        this.scanId = scanId;
        this.path = path;
        this.fileRowCreator = fileRowCreator;
        this.fileBeanFactory = new FileBeanFactory(scanId, fileIdSequence);

    }

    public void walk() throws IOException {
        Files.walkFileTree(path, this);
    }

    private BasicFileAttributes readAttributes(Path path) throws IOException {
        return Files.readAttributes(path, BasicFileAttributes.class);
    }

    private void logDirChange(String verb, Path dir, Long dirId, Long parentDirId) {
        log.info(verb + " " + dir);
        if (parentDirId == null) {
            log.debug("FileId of " + dir + " is " + dirId + " and it is a scan root directory");
        } else {
            log.debug("FileId of " + dir + " is " + dirId + " and parent dir id is " + parentDirId);
        }
    }

    /**
     * Invoked for a directory logBefore entries in the directory are visited.
     *
     * <p> Unless overridden, this method returns {@link FileVisitResult#CONTINUE
     * CONTINUE}.
     */
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attributes) {

        FileBean fileBean = fileBeanFactory.create(dirId, scanId, dir, attributes);
        fileRowCreator.create(fileBean);

        dirIdStack.push(dirId);

        logDirChange("Entering", dir, fileBean.getFileId(), dirId);

        dirId = fileBean.getFileId();

        dirCount++;

        return FileVisitResult.CONTINUE;

    }

    /**
     * Invoked for a file in a directory.
     *
     * <p> Unless overridden, this method returns {@link FileVisitResult#CONTINUE
     * CONTINUE}.
     */
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {

        if (!attributes.isDirectory()) {

            FileBean fileBean = fileBeanFactory.create(dirId, scanId, file, attributes);

            fileRowCreator.create(fileBean);

            fileCount++;

            log.info(file.toString());

            log.debug("FileId of file is " + fileBean.getFileId() + " and id of its dir " + dirId);

        }
        return FileVisitResult.CONTINUE;

    }

    /**
     * Invoked for a file that could not be visited.
     *
     * <p> Unless overridden, this method re-throws the I/O logException that prevented
     * the file from being visited.
     */
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException ioException) {
        Objects.requireNonNull(file);
        log.warn("Visit of " + file + " failed " + ioException.getMessage());
        return FileVisitResult.CONTINUE;
    }

    /**
     * Invoked for a directory logAfter entries in the directory, and all of their descendants, have been visited.
     *
     * <p> Unless overridden, this method returns {@link FileVisitResult#CONTINUE
     * CONTINUE} if the directory iteration completes without an I/O logException; otherwise this method re-throws the
     * I/O logException that caused the iteration of the directory to terminate prematurely.
     */
    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException ioException) throws IOException {

        dirId = dirIdStack.pop();

        logDirChange("Leaving", dir, dirId, dirIdStack.isEmpty() ? null : dirIdStack.peek());

        return FileVisitResult.CONTINUE;
    }

}
