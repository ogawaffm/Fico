package com.ogawa.fico;

import com.ogawa.fico.application.FileBean;
import com.ogawa.fico.application.FileBeanFactory;
import com.ogawa.fico.db.FileRowCreator;
import com.ogawa.fico.db.FileRowMapper;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import java.util.Stack;

public class PersistingFileVisitor implements FileVisitor<Path> {

    private final Path path;
    private final FileRowCreator fileRowCreator;
    private final int scanId;

    private Long dirId = null;

    private Stack<Long> dirIdStack = new Stack<>();

    public PersistingFileVisitor(int scanId, Path path, FileRowCreator fileRowCreator) {

        this.scanId = scanId;
        this.path = path;
        this.fileRowCreator = fileRowCreator;

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

        FileBean fileBean = FileBeanFactory.create(dirId, scanId, dir, attributes);
        Long fileId = fileRowCreator.create(fileBean);

        dirIdStack.push(dirId);

        System.out.println("pre  " + fileId + ":" + (dirId == null ? -1 : dirId) + " " + dir);

        dirId = fileId;

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

            FileBean fileBean = FileBeanFactory.create(dirId, scanId, file, attributes);
            Long fileId = fileRowCreator.create(fileBean);

            System.out.println("     " + fileId + ":" + dirId + " " + file);
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
