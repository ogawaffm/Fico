package com.ogawa.fico.scan.fileformat.image;

import com.drew.imaging.FileType;
import com.ogawa.fico.service.FileNaming;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MediaMetadataReader {

    private final static List<String> supportedFileTypes = new ArrayList<>();

    static {
        for (FileType fileType : FileType.values()) {
            if (fileType.getAllExtensions() != null) {
                for (String extension : fileType.getAllExtensions()) {
                    supportedFileTypes.add(extension.toLowerCase());
                }
            }
        }
    }

    static public boolean isSupportedFileType(Path filename) {
        return supportedFileTypes.contains(FileNaming.getFilenameExtension(filename.toString()).toLowerCase());
    }

}
