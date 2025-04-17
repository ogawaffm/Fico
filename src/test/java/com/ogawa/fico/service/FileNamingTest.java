package com.ogawa.fico.service;

import org.junit.jupiter.api.Test;

public class FileNamingTest {

    private final String slash = "\\";

    private final String doubleSlash = slash + slash;

    private final String validServerName = "server";
    private final String validServerAddress = doubleSlash + validServerName;
    private final String validShareOnly = "share";
    private final String fileOnlyName = "file";
    private final String fullNetShare = validServerAddress + slash + validShareOnly;
    private final String fullNetShareFile = validServerAddress + slash + validShareOnly + slash + fileOnlyName;

    private final String driveSpec = "c:";
    private final String fullLocalDriveShare = doubleSlash + "?" + slash + driveSpec;

    private final String fullLocalDriveShareFile = doubleSlash + "?" + slash + driveSpec + slash + fileOnlyName;


    @Test
    public void testIsUnc() {

//        assertFalse(FileNaming.isUnc(validServerName));
//        assertFalse(FileNaming.isUnc(validServerAddress));
//        assertFalse(FileNaming.isUnc(validShareOnly));
//        assertFalse(FileNaming.isUnc(fileOnlyName));
//
//        assertTrue(FileNaming.isUnc(fullNetShare));
//        assertTrue(FileNaming.isUnc(fullNetShareFile));

    }

}
