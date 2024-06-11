package com.ogawa.ficoold;

import com.ogawa.ficoold.db.FilePersister;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import java.util.Stack;

public class PersistingFileVisitor<ID_TYPE> implements FileVisitor<Path> {

    private final Path path;
    private final FilePersister<ID_TYPE> filePersister;

    private ID_TYPE dirId = null;

    private Stack<ID_TYPE> dirIdStack = new Stack<>();

    public PersistingFileVisitor(Path path, FilePersister<ID_TYPE> filePersister) {

        this.path = path;
        this.filePersister = filePersister;

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

        ID_TYPE fileId = filePersister.persist(dirId, dir, attributes);

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
            ID_TYPE fileId = filePersister.persist(dirId, file, attributes);
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

        System.out.println("post " + dirId + ":" + (dirIdStack.isEmpty() ? -1 : dirIdStack.peek()) + " " + dir);

        return FileVisitResult.CONTINUE;
    }

}
