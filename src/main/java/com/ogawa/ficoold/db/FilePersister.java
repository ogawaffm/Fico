package com.ogawa.ficoold.db;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public interface FilePersister<ID_TYPE> {

    ID_TYPE persist(ID_TYPE parentDirId, Path path, BasicFileAttributes attributes);

}
