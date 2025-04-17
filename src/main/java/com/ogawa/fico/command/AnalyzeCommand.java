package com.ogawa.fico.command;

import static com.ogawa.fico.db.Util.getSql;
import static com.ogawa.fico.db.Util.getValue;

import com.ogawa.fico.command.argument.CommandWithOneOptionalArg;
import com.ogawa.fico.db.persistence.beanreader.BeanReader;
import com.ogawa.fico.db.persistence.factory.ScanPersistenceFactory;
import com.ogawa.fico.performance.logging.Formatter;
import com.ogawa.fico.scan.ScanBean;
import com.ogawa.fico.service.CalcMode;
import com.ogawa.fico.service.ChecksumCalcService;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;

// TODO: Make scanId variable
@Slf4j
public class AnalyzeCommand extends DatabaseCommand implements CommandWithOneOptionalArg {

    static final public String KEY_WORD = "analyze";

    AnalyzeCommand(String[] commandArguments) {
        super(commandArguments);
    }

    @Override
    public String getName() {
        return KEY_WORD;
    }

    private CalcMode determinCalcMode() {
        if (getArgumentCount() == 1) {
            String modeArg = getArgument(0);
            return Arrays.stream(CalcMode.values())
                .filter((mode) -> mode.getKeyWord().equals(modeArg))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown " + KEY_WORD + " argument: " + modeArg));
        }
        return CalcMode.SAME_NAME_AND_SIZE;
    }

    public void execute() {

        Connection connection = getConnection();
        CalcMode calcMode = determinCalcMode();

        try {

            ScanPersistenceFactory scanPersistenceFactory = new ScanPersistenceFactory(connection);
            BeanReader<ScanBean> beanReader = scanPersistenceFactory.createTableReader();

            Long[] allFinishedScanIds = beanReader.stream()
                .filter((scan) -> scan.getFinished() != null)
                .map(ScanBean::getScanId)
                .sorted()
                .toArray(Long[]::new);

            ChecksumCalcService checkSummer = new ChecksumCalcService(connection, calcMode, allFinishedScanIds);
            checkSummer.calc();

            if (calcMode == CalcMode.CLEAR) {
                log.info("Analyze cleared {} checksums of files and directories", checkSummer.getFileCount());
            } else {
                String sql = getSql("SelectFileCount");
                double totalFileCount = getValue(connection, sql, 0.0, allFinishedScanIds, false);
                double totalDirCount = getValue(connection, sql, 0.0, allFinishedScanIds, true);

                log.info("Analyze analyzed {} files and {} directories"
                        + " which are {} of all files and {} of all directories",
                    checkSummer.getFileCount(), checkSummer.getDirCount(),
                    Formatter.formatAsPercentage(totalFileCount / checkSummer.getFileCount()),
                    Formatter.formatAsPercentage(totalDirCount / checkSummer.getDirCount())
                );
                // TODO commit is set by BATCHER
                //  connection.commit();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
