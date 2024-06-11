package com.ogawa.fico.application;

import com.ogawa.fico.PersistingFileVisitor;
import com.ogawa.fico.db.FileRowCreator;
import com.ogawa.fico.db.Model;
import com.ogawa.fico.db.ScanRowWriter;
import com.ogawa.fico.db.Util;
import com.ogawa.fico.performance.logging.building.builder.LoggingActionBuilder;
import com.ogawa.fico.performance.logging.DurationFormatter;
import com.ogawa.fico.performance.measuring.StopWatch;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.LoggerFactory;

public class Main {

    public static String fortyTwo = "42";

    static public Connection getConnection() {
        return null;
    }

    public static String getEightyFour() {
        return "84";
    }

    public static boolean test(Supplier arg) {
        return arg == null;
    }

    public static int test(Function function) {
        return 42;
    }

    public static void test() {
        boolean b = test(() -> null);
        int i = test(o -> 42);
    }

    public static void consumer(String s) {

    }

    public static int testFunc(int arg) {
        return arg * 2;
    }

    public static void main(String[] args) {

        try {

            Config config = new Config(args);

            StopWatch stopWatch = StopWatch.create();
            stopWatch.start();
            stopWatch.setName("Total time");
            System.out.println("Started at " + stopWatch.getStartTimeDate());

            StopWatch stepStopWatch = StopWatch.create();

            new LoggingActionBuilder().logger(LoggerFactory.getLogger(Main.class))
                .invoke(Main::consumer)
                .build().accept("Hello");

            new LoggingActionBuilder().logger(LoggerFactory.getLogger(Main.class))
                .batch((Iterator) null)
                .logBefore("batch")
                .logAfter("batch")
                .logException("${Exception}")

                .group(100)
                .logBefore("new batch")
                .logAfter("batch done")
                .logException("${Exception}")

                .invoke(Main::consumer)
                .logBefore("new batch")
                .logAfter("batch done")
                .logException("${Exception}")
                .build().accept("1");
//
//            int dbl = new LoggingActionBuilder().logger(LoggerFactory.getLogger(Main.class))
//                .invoke(Main::testFunc)
//                .batch((Iterator) null)

            Connection connection1 = new LoggingActionBuilder().logger(LoggerFactory.getLogger(Main.class))
                .invoke(Util::getTcpConnection)
                .arguments("DatabaseName")
                .logBefore("logBefore")
                .logAfter("logAfter")
                .logException("logException ${Exception} during initial invocation")

                .checkIsNotNull()
                .logGood("logGood")
                .logPoor("logPoor")
                .logException("logException ${Exception} during adjustment")

                .adjustIfNull(Util::getTcpConnection)

                .logBefore("logBefore")
                .logAfter("logAfter")
                .logGood("logGood")
                .logPoor("logPoor")

                .correctError((Connection) null)
                .logBefore("Caught ${Exception}")
                .logAfter("logAfter")
                .build().apply("DatabaseName");

            //Connection connection = new LoggingActionBuilder()
            Connection connection = new LoggingActionBuilder().logger(LoggerFactory.getLogger(Main.class))
                .batch((Iterator) null).invoke(Main::getConnection)
                .invoke(Util::getTcpConnection)
                .arguments("DatabaseName")
                .logBefore("Connecting to h2 database '${DatabaseName}' ...")
                .checkIsNotNull()
                .logGood("Successfully connected using tcp to h2 database '${DatabaseName}'")
                .logPoor("Could not connect using tcp, trying via local file '${DatabaseName}'")

                .adjustIf((s, connection2) -> true, Util::getFileConnection)
                .logBefore("Trying via local file '${DatabaseName}'")
                .logGood("Successfully connected via local file to h2 database '${DatabaseName}'")
                .logPoor("Could not connect to h2 database '${DatabaseName}'")

                .correctError((Connection) null)
                .check(y -> true)
                .logGood("")
                .logPoor("")
                .logAfter("")
                .logException("Could not connect to h2 database '${DatabaseName}' due to ${Exception}")
                .build().apply("Files");

            System.exit(42);
            stepStopWatch.start();
            stepStopWatch.stop();

            System.out.println("Connected to h2 database '" + config.getDatabaseName()
                + "' using " + connection.toString() + " in "
                + new DurationFormatter().format(stepStopWatch.getAccumulatedRecordedTime()));

            long fileCount = 0;

            try {

                Model model = new Model(connection);

                model.createModel();

                ScanRowWriter scanRowWriter = new ScanRowWriter(connection);

                int scanId = scanRowWriter.create(config.getRootPath());

                FileRowCreator fileRowCreator = new FileRowCreator(connection, scanId);

                PersistingFileVisitor fileVisitor = new PersistingFileVisitor(scanId, config.getRootPath(),
                    fileRowCreator);

                scanRowWriter.updateStarted(scanId, new Date());
                fileVisitor.walk();
                fileRowCreator.close();
                scanRowWriter.updateFinished(scanId, new Date());

//            CheckSummer checkSummer = new CheckSummer();
                ChecksumCalcService checkSummer = new ChecksumCalcService();
                fileCount = checkSummer.sum(connection, scanId);

            } catch (Exception exception) {
                exception.printStackTrace();
            }

            try {
                try {
                    connection.commit();
                } catch (SQLException e) {
                    throw new RuntimeException("Could not commit local h2 database '" + config.getDatabaseName() + "'.",
                        e);
                }
                connection.close();
            } catch (SQLException sqlException) {
                throw new RuntimeException("Could not close local h2 database '" + config.getDatabaseName() + "'.",
                    sqlException);
            }

            System.out.println("Finished at " + new Date() + ". Calculated checksums for " + fileCount + " files in "
                + new DurationFormatter().format(stopWatch.getAccumulatedRecordedTime()) + "."
            );

        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

}
