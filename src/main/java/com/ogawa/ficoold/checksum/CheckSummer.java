package com.ogawa.ficoold.checksum;

import com.ogawa.ficoold.db.Util;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CheckSummer {

    public int sum(Connection connection) {

        Date start = new Date();
        System.out.println("Marking duplicate candidates...");
        int markedRowCount = Util.markDuplicateCandidates(connection);
        System.out.println("markedRowCount: " + markedRowCount + " in "
            + (new Date().getTime() - start.getTime()) * 1000 + " s");

        ResultSet resultSet = Util.getMarkedDuplicateCandidates(connection);
        Path path = null;
        byte[] checksum = null;
        int fileCount = 0;

        try {
            while (resultSet.next()) {
                path = Path.of(resultSet.getString("PATH"), resultSet.getString("NAME"));
                System.out.print(path + " ");
                FileChecksumBuilder fileChecksumBuilder = new FileChecksumBuilder("SHA-256", 64 * 1024 * 1024);
                checksum = fileChecksumBuilder.update(path).getBinaryChecksum();
                System.out.println(fileChecksumBuilder.getHexChecksum());
                resultSet.updateBytes("CHECKSUM", checksum);
                resultSet.updateRow();
                fileCount++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return fileCount;

    }

}
