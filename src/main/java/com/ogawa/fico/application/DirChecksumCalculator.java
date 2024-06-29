package com.ogawa.fico.application;

import static com.ogawa.fico.db.Util.execAndReturnRowsAffected;
import static com.ogawa.fico.db.Util.getSql;

import com.ogawa.fico.performance.logging.DurationFormatter;
import com.ogawa.fico.performance.measuring.StopWatch;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DirChecksumCalculator {

    static private final String UPDATE_DIRECTORY_CHECKSUM = "UpdateDirectoryChecksum";

    static private long executeUpdate(Connection connection) {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(getSql(UPDATE_DIRECTORY_CHECKSUM));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return execAndReturnRowsAffected(preparedStatement);
    }

    static public long calc(Connection connection) {

        long updateCount = 0;
        long totalUpdatedDirCount = 0;
        long updatedDirCount;

        StopWatch stopWatch = StopWatch.create();
        stopWatch.pause();

        System.out.println("Calculating directory checksums...");

        do {
            updateCount++;

            stopWatch.resume();
            updatedDirCount = executeUpdate(connection);
            stopWatch.pause();

            totalUpdatedDirCount = totalUpdatedDirCount + updatedDirCount;

            System.out.println("#" + updateCount + ": " + updatedDirCount + " in " + stopWatch.getLastRecordedTime());

        } while (updatedDirCount > 0);

        stopWatch.stop();

        System.out.println("total dirs with checksum: " + totalUpdatedDirCount + " in "
            + new DurationFormatter().format(stopWatch.getAccumulatedRecordedTime())
            + " in " + updateCount + " updates"
        );

        return totalUpdatedDirCount;

    }


}
