package com.ogawa.fico.service;

import lombok.Getter;

public enum CalcMode {
    CLEAR("clear", "UpdateChecksumByNull", "Clear file checksums"),
    ALL("all", "SelectAllFiles", "all files"),
    SAME_SIZE("size", "SelectSameSizeFiles",
        "files for which at least one file with the same size exists"),
    SAME_NAME_AND_SIZE("appearance", "SelectSameNameSameSizeFiles",
        "files for which at least one file with the same name and size exists"),
    SAME_SIZE_IN_SAME_DIR("dir", "SelectSameSizeInSameDirFiles",
        "files for which at least one file in the same directory with the same size exists"),
    ;

    @Getter
    private final String keyWord;

    @Getter
    private final String SqlName;

    @Getter
    private final String description;

    CalcMode(String keyWord, String SqlName, String description) {
        this.keyWord = keyWord;
        this.SqlName = SqlName;
        this.description = description;
    }
}
