package performance.logging;

import com.ogawa.fico.messagetemplate.MessagePreparator;
import com.ogawa.fico.messagetemplate.MessageTemplate;
import com.ogawa.fico.messagetemplate.appender.VariableAppender;
import com.ogawa.fico.performance.logging.ActionLogger;
import com.ogawa.fico.performance.logging.building.builder.LoggingActionBuilder;
import com.ogawa.fico.performance.logging.ProtectedVariableAppenders;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.slf4j.event.Level.*;

import utility.TestUtils;


public class SimpleProgressLoggingTests {

    private String func() {
        return ">" + System.currentTimeMillis() + "<";
    }

    private void funcX(ActionLogger pl, StringBuilder sb) {
        sb.append(">Stepno " + pl.getStepNumber() + "<");
    }

    Integer var = 0;

    @Test
    public void withOwnVariable() {

        Consumer<Integer> consumer = new LoggingActionBuilder()
            .logger(LoggerFactory.getLogger(SimpleProgressLoggingTests.class))
            .invoke(createThrowingIntegerConsumer(-1))
            .logAfter("started at ${StartTime} and finished at ${FinishTime} logAfter ${Duration}")
            .build();

        consumer.accept(1);
    }

    @Test
    public void showAllVariables() {

        LoggingActionBuilder builder = new LoggingActionBuilder();

        TreeMap<String, VariableAppender<ActionLogger>> sortedDefaultTemplateVariables
            = new TreeMap<>(ProtectedVariableAppenders.DEFAULT_VARIABLE_APPENDERS);

        MessagePreparator<ActionLogger> messagePreparator = new MessagePreparator<>(
            ProtectedVariableAppenders.DEFAULT_VARIABLE_APPENDERS.values(), Collections.EMPTY_LIST
        );

        for (Map.Entry<String, VariableAppender<ActionLogger>> entry : sortedDefaultTemplateVariables.entrySet()) {

            String variableName = entry.getKey();

            builder.invoke(createThrowingIntegerConsumer(-1)).
                logBefore(variableName + ": " + MessageTemplate.createVariableReference(variableName));

        }

        Consumer<Integer> consumer = builder
            .logger(LoggerFactory.getLogger(LOGGER_NAME))
            .invoke(createThrowingIntegerConsumer(-1))
            .build();

        accept(consumer, 0, 20);

    }

    private static final String LOGGER_NAME = SimpleProgressLoggingTests.class.toString();

    private void accept(Consumer<Integer> c, int from, int to) {
        for (int i = from; i < to; i++) {
            TestUtils.waitMillis(1);
            c.accept(i);
        }
    }

    private void accept(Consumer<Integer> c, int i) {
        c.accept(i);
    }

    private Consumer<Integer> createThrowingIntegerConsumer(int throwAt) {
        return (i) -> {
            if (i == throwAt) {
                throw new RuntimeException("i = " + throwAt);
            }
            TestUtils.waitMillis(1);
        };
    }

    @Test
    public void test() {

        Logger logger = LoggerFactory.getLogger(LOGGER_NAME);

        int THROW_EXCEPTION_AT = 3210;

        Consumer<Integer> c = new LoggingActionBuilder()
            .logger(logger)
            .throughputTimeUnit(ChronoUnit.SECONDS)
            .batchLogLevel(DEBUG)
            .invoke(createThrowingIntegerConsumer(THROW_EXCEPTION_AT))
            .logBefore("starting invoke")
            .logAfter("finished logAfter ${TotalTime} using ${TotalProcessingTime} for"
                + " ${TotalProcessedUnits} units (${TotalThroughputUnits}/${TotalThroughputTimeUnit})")

//            .logBatchFinish("progress TotalProcessedUnits: ${TotalProcessedUnits} "
//                + "TotalProcessingTime: ${TotalProcessingTime} TotalThroughputUnits: ${TotalThroughputUnits}"
//                + " BatchProcessingTime: ${BatchProcessingTime} BatchThroughputUnits: ${BatchThroughputUnits}")

            .logBefore("${StepNumber}")
            .logBefore("${OsName} ${OsVersion} ${OsArchitecture} ${JavaVendor} ${JavaVersion}")

            .logException(
                "Caught ${ThrowableMessage} caused by ${ThrowableCause} logAfter ${TotalTime} and ${TotalProcessedUnits} units. Stack: ${ThrowableStackTrace}")
            .logException("aborted logAfter ${TotalTime} using ${TotalProcessingTime} for"
                + " ${TotalProcessedUnits} units (${TotalThroughputUnits}/${TotalThroughputTimeUnit}) due to an logException (${ThrowableMessage}, ${ThrowableCause}))")
            .build();

        accept(c, 1, 10000);

    }

    static public void main(String[] args) {
        SimpleProgressLoggingTests test = new SimpleProgressLoggingTests();
        test.showAllVariables();
        //        test.test();
    }

}
