package com.ogawa.fico.db.persistence.rowmapper;

import com.ogawa.fico.scan.FileBean;
import com.ogawa.fico.scan.FileBeanFactory;
import java.nio.file.Path;
import java.util.List;

import static com.ogawa.fico.db.persistence.rowmapper.RowCaster.*;

public class FileRowMapper implements RowMapper<FileBean> {

    private static final List<String> COLUMN_NAMES = List.of(
        "FILE_ID",
        "DIR_ID",
        "SCAN_ID",
        "PATH",
        "NAME",
        "SIZE",
        "IS_DIR",
        "FILES_CONTAINED",
        "DIRS_CONTAINED",
        "MODIFICATION_TIME",
        "CREATION_TIME",
        "CHECKSUM",
        "CALC_STARTED",
        "CALC_FINISHED");

    private static final List<String> PRIMARY_KEY_COLUMN_NAMES = List.of("FILE_ID");

    @Override
    public List<String> getColumnNames() {
        return COLUMN_NAMES;
    }

    public List<String> getPrimaryKeyColumnNames() {
        return PRIMARY_KEY_COLUMN_NAMES;
    }


    @Override
    public Object[] toRow(FileBean fileBean) {
        return new Object[]{
            fileBean.getDirId(),
            fileBean.getScanId(),
            getDirectoryName(fileBean.getFullFileName()),
            getFilename(fileBean.getFullFileName()),
            fileBean.getSize(),
            fileBean.getIsDirectory(),
            fileBean.getFilesContained(),
            fileBean.getDirsContained(),
            toTimestamp(fileBean.getModificationTime()),
            toTimestamp(fileBean.getCreationTime()),
            fileBean.getChecksum(),
            toTimestamp(fileBean.getCalcStarted()),
            toTimestamp(fileBean.getCalcFinished()),

            // Primary key
            fileBean.getFileId()
        };
    }

    @Override
    public Object[] getPrimaryKeyValues(FileBean object) {
        return new Object[]{object.getFileId()};
    }

    @Override
    public void setPrimaryKeyValues(FileBean object, Object[] primaryKeyValues) {
        object.setFileId(toLongValue(primaryKeyValues[0]));
    }

    @Override
    public FileBean toObject(Object[] row) {
        FileBean fileBean = FileBeanFactory.create(

            toLongValue(row[13]),

            toLongValue(row[0]),
            toLongValue(row[1]),
            getFullPath((String) row[2], (String) row[3]),
            toLongValue(row[4]),
            (boolean) row[5],
            toIntegerValue(row[6]),
            toIntegerValue(7),
            toLocalDateTime((java.sql.Timestamp) row[8]),
            toLocalDateTime((java.sql.Timestamp) row[9]),
            (byte[]) row[10],
            toLocalDateTime((java.sql.Timestamp) row[11]),
            toLocalDateTime((java.sql.Timestamp) row[12])
        );
        return fileBean;
    }


    private static String getDirectoryName(Path path) {
        if (path == null || path.getParent() == null) {
            return "";
        } else {
            return path.getParent().toString();
        }
    }

    private static String getFilename(Path path) {
        if (path == null || path.getFileName() == null) {
            return "";
        } else {
            return path.getFileName().toString();
        }
    }

    static Path getFullPath(String path, String name) {
        if (path == null || name == null) {
            return null;
        } else {
            return Path.of(path, name);
        }
    }

}
