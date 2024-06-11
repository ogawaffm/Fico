package com.ogawa.ficoold.db;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

public class DbFilePersister implements FilePersister<Integer> {

    private final FileRowWriter fileRowWriter;

    public DbFilePersister(FileRowWriter fileRowWriter) {
        this.fileRowWriter = fileRowWriter;
    }

    public Integer persist(Integer parentDirId, Path path, BasicFileAttributes attributes) {
        return fileRowWriter.create(
            parentDirId,
            path.getFileName() == null ? "" : path.getParent().toString(),
            path.getFileName() == null ? "" : path.getFileName().toString(),
            attributes.isDirectory() ? -1 : attributes.size(),
            new Date(attributes.lastModifiedTime().toMillis())
        );
    }

}
