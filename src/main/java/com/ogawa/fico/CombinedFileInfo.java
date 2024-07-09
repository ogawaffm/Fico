package com.ogawa.fico;

import com.ogawa.fico.checksum.ChecksumStats;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(doNotUseGetters = true)
@Getter
public class CombinedFileInfo implements Comparable<CombinedFileInfo> {

    private final Path path;
    private final BasicFileAttributes attributes;
    private final ChecksumStats checksumStats;

    public CombinedFileInfo(Path path, BasicFileAttributes attributes, ChecksumStats checksumStats) {
        this.path = path;
        this.attributes = attributes;
        this.checksumStats = checksumStats;
    }

    @Override
    public int compareTo(CombinedFileInfo o) {

        int c;

        c = path.compareTo(o.path);
        if (c != 0) {
            return c;
        }

        c = Long.compare(
            attributes.creationTime().toMillis(), o.attributes.creationTime().toMillis()
        );
        if (c != 0) {
            return c;
        }

        c = Long.compare(
            attributes.lastModifiedTime().toMillis(), o.attributes.lastModifiedTime().toMillis()
        );
        if (c != 0) {
            return c;
        }

        c = Long.compare(
            attributes.lastAccessTime().toMillis(), o.attributes.lastAccessTime().toMillis()
        );
        if (c != 0) {
            return c;
        }

        c = Long.compare(attributes.size(), o.attributes.size());
        if (c != 0) {
            return c;
        }

        c = Boolean.compare(attributes.isRegularFile(), o.attributes.isRegularFile());
        if (c != 0) {
            return c;
        }

        c = Boolean.compare(attributes.isDirectory(), o.attributes.isDirectory());
        if (c != 0) {
            return c;
        }

        c = Boolean.compare(attributes.isSymbolicLink(), o.attributes.isSymbolicLink());
        if (c != 0) {
            return c;
        }

        c = Boolean.compare(attributes.isOther(), o.attributes.isOther());
        if (c != 0) {
            return c;
        }

        // maybe there is more internally of the attributes
        c = Integer.compare(attributes.hashCode(), o.attributes.hashCode());
        if (c != 0) {
            return c;
        }

        c = checksumStats.compareTo(o.checksumStats);
        if (c != 0) {
            return c;
        }

        return 0;

    }

}



