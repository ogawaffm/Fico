package com.ogawa.fico.application;

import static com.ogawa.fico.db.Util.execAndReturnRowsAffected;
import static com.ogawa.fico.db.Util.getSql;

import com.ogawa.fico.performance.logging.Formatter;
import com.ogawa.fico.performance.measuring.StopWatch;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileCandidateMarker {

    static private final String RESET_MARKED_DUPLICATE_CANDIDATES = "ResetMarkedDuplicateCandidates";

    static private final String CREATE_DUPLICATE_CANDIDATES = "CreateDuplicateCandidates";

    static private final String MARK_DUPLICATE_CANDIDATES = "MarkDuplicateCandidates";

    private static long createDuplicateCandidates(Connection connection) {

        try {

            PreparedStatement preparedStatement;
            preparedStatement = connection.prepareStatement(getSql(RESET_MARKED_DUPLICATE_CANDIDATES));

            execAndReturnRowsAffected(preparedStatement);

            preparedStatement = connection.prepareStatement(getSql(CREATE_DUPLICATE_CANDIDATES));

            execAndReturnRowsAffected(preparedStatement);

            preparedStatement = connection.prepareStatement(getSql(MARK_DUPLICATE_CANDIDATES));
            //Takes about 2 minutes to run logEvent 1 million rows
            return execAndReturnRowsAffected(preparedStatement);

        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    public static void mark(Connection connection) {
        StopWatch stopWatch = StopWatch.create();
        stopWatch.start();

        log.info("Marking duplicate candidates...");
        long markedRowCount = createDuplicateCandidates(connection);

        stopWatch.stop();

        log.info("Marked " + markedRowCount + " files as candidate in "
            + Formatter.format(stopWatch.getAccumulatedRecordedTime())
        );

    }


}
