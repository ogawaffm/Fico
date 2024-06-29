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
import java.sql.Connection;
import java.util.Objects;
import java.util.Stack;

public class PersistingFileVisitor implements FileVisitor<Path> {

    private final Path path;

    private final FileBeanFactory fileBeanFactory;
    private final FileRowCreator fileRowCreator;
    private final long scanId;

    private Long dirId = null;

    private Stack<Long> dirIdStack = new Stack<>();

    private long fileCount = 0;
    private long dirCount = 0;

    public PersistingFileVisitor(long scanId, Path path, Sequence fileIdSequence, FileRowCreator fileRowCreator) {

        this.scanId = scanId;
        this.path = path;
        this.fileRowCreator = fileRowCreator;
        this.fileBeanFactory = new FileBeanFactory(scanId, fileIdSequence);

    }

    public long getFileCount() {
        return fileCount;
    }

    public long getDirCount() {
        return dirCount;
    }

    public void walk() throws IOException {
        Files.walkFileTree(path, this);
    }

    private BasicFileAttributes getAttributes(Path path) throws IOException {
        return Files.readAttributes(path, BasicFileAttributes.class);
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

        System.out.println("pre  " + fileBean.getFileId() + ":" + (dirId == null ? -1 : dirId) + " " + dir);

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

            if (fileBean.getFullFileName().endsWith("catalog_24.png")) {
                fileBean = fileBean;
            }

            fileRowCreator.create(fileBean);

            fileCount++;

            System.out.println("     " + fileBean.getFileId() + ":" + dirId + " " + file);

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
        System.out.print('\t');
        System.out.println(ioException.getMessage());
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

        System.out.println(
            "post " + dirId + ":" + (dirIdStack.isEmpty() ? Long.valueOf(-1) : dirIdStack.peek()) + " " + dir);

        return FileVisitResult.CONTINUE;
    }

}
