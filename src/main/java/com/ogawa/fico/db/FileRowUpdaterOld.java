package com.ogawa.fico.db;

import com.ogawa.ficoold.checksum.BytesChecksumBuilder;
import java.sql.Connection;
import java.time.LocalDateTime;

public class FileRowUpdaterOld extends FileRowBatchAccess {

    private final static String UPDATE_FILE_CHECKSUM =
        "UPDATE FILE SET CHECKSUM = ?, CALC_STARTED = ?, CALC_FINISHED = ? WHERE FILE_ID = ?";

    public FileRowUpdaterOld(Connection connection, int scanId) {

        super(connection, scanId, UPDATE_FILE_CHECKSUM, 1000, true);

    }

    public void updateCheckSum(long fileId, byte[] checksum, LocalDateTime calcStarted, LocalDateTime calcFinished) {
        try {

            batchRowWriter.setRow(new Object[]{
                checksum,
                calcStarted,
                calcFinished,
                fileId
            });

        } catch (Exception e) {
            System.out.println(
                "checksum: " + BytesChecksumBuilder.getBytesToHex(checksum)
                    + " calcStarted: " + calcStarted + " calcFinished: " + calcFinished +
                    " fileId: " + fileId);
            throw new RuntimeException(e);
        }
    }

}
