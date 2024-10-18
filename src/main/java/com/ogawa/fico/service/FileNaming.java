package com.ogawa.fico.service;

import java.util.regex.Pattern;

public class FileNaming {

    static boolean isUnc(String filename) {
        return filename.startsWith("\\\\");
    }

    /**
     * Check if the filename is a local UNC path, which is a UNC path that starts with "\\?\E:".
     *
     * @param filename
     * @return
     */
    static boolean isLocalUnc(String filename) {
        return filename.startsWith("\\\\?\\")
            && filename.length() > 5 && filename.charAt(5) != ':'
            && isValidDriverChar(filename.charAt(4));
    }

    /**
     * Check if the filename is a local path, which is a path that starts with e.g. "E:" or a local UNC path that starts
     * e.g. with "\\?\E:".
     *
     * @param filename
     * @return
     */
    static boolean isLocal(String filename) {
        return startsWithDriveSpec(filename) || isLocalUnc(filename);
    }

    static boolean isValidDriverChar(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    /**
     * Check if the filename starts with a drive letter, e.g. "E:".
     *
     * @param filename
     * @return true if the filename starts with a drive letter
     */
    static boolean startsWithDriveSpec(String filename) {
        return filename.length() > 1 && filename.charAt(1) == ':' && isValidDriverChar(filename.charAt(0));
    }

    static String getDriverSpec(String filename) {
        if (isLocal(filename)) {
            if (isLocalUnc(filename)) {
                return filename.substring(4, 7);
            } else {
                return filename.substring(0, 2);
            }
        } else {
            throw new IllegalArgumentException("Not a local path: " + filename);
        }
    }

    static String getServer(String filename) {
        if (isUnc(filename)) {
            // cut off the leading "\\"
            String unc = filename.substring(2);
            // find the first backslash
            int slash = unc.indexOf('\\');
            return unc.substring(0, slash);
        } else {
            throw new IllegalArgumentException("Not a UNC path: " + filename);
        }
    }

    static String getShareName(String filename) {
        if (isUnc(filename)) {
            String server = getServer(filename);
            if (filename.length() > 2 + server.length() + 1) {
                String shareWithFilename = filename.substring(2 + server.length() + 1) + '\\';
                return shareWithFilename.substring(0, shareWithFilename.indexOf('\\'));
            } else {
                throw new IllegalArgumentException("No share name in UNC path: " + filename);
            }
        } else {
            throw new IllegalArgumentException("Not a UNC path: " + filename);
        }
    }

    static String getUnc(String filename) {
        if (isUnc(filename)) {
            return "\\\\" + getServer(filename) + "\\" + getShareName(filename);
        } else {
            throw new IllegalArgumentException("Not a UNC path: " + filename);
        }
    }

    public static String getFilenameExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        // if the last dot is after the last backslash, we have a file extension
        if (lastDot > 0 && filename.lastIndexOf('\\') < lastDot) {
            return filename.substring(lastDot + 1);
        } else {
            return "";
        }
    }

    private static String getFilenameWithoutDriveSpecAndUnc(String filename) {
        if (startsWithDriveSpec(filename)) {
            return filename.substring(2);
        } else if (isLocalUnc(filename)) {
            return filename.substring(6);
        } else {
            String server = getServer(filename);
            String share = getShareName(filename);
            return filename.substring(2 + server.length() + 1 + share.length());
        }
    }

    static String getFilenameWithoutExtension(String filename) {
        String name = getFilenameWithoutDriveSpecAndUnc(filename);
        int dot = name.lastIndexOf('.');
        if (dot > 0) {
            return name.substring(0, dot);
        } else {
            return name;
        }
    }

    private static final Pattern recycleBinPattern = Pattern.compile(
        // since on windows the recycle bin is case-insensitive, we have to use the case-insensitive flag
        "(?i)RECYCLED|RECYCLER|\\$RECYCLE\\.BIN(?-i)|\\.Trash|\\.Trashes|[@#\\.]recycle|@Recycle|Recycle Bin(((-Volume)| )\\d+)*");

    // @formatter:off
    /**
     * Check if the filename is a recycle bin. The following names are recognized as recycle bins:
         "RECYCLED"             FAT
         "RECYCLER"             NTFS filesystem (Windows 2000, XP, NT)
         "$RECYCLE.BIN"         Windows Vista and above
         ".Trash"               Mac user trash
         ".Trashes"             Mac volume trash
         "trashbox"             Buffalo NAS
         "@recycle"             ASUS NAS
         "@Recycle"             QNAP NAS
         "#recycle"             Synology NAS, TerraMaster NAS
         ".recycle"             Samba Share (recommended by Samba)
         "Recycle Bin"          Netgear NAS
         "Recycle Bin X"        Asustor NAS
         “Recycle Bin-VolumeX”  Western Digital NAS
     *
     * @param filename
     * @return
     */
    // @formatter:on
    public static boolean isRecycleBin(String filename) {
        return recycleBinPattern.matcher(getFilenameWithoutDriveSpecAndUnc(filename)).find();
    }

}
