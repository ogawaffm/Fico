package com.ogawa.fico.performance.logging;

import com.ogawa.fico.messagetemplate.appender.VariableAppender;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.event.Level;

public class ProtectedVariableAppenders {

    @Getter
    @Setter
    private Level defaultLogLevel = Level.INFO;

    /**
     * Creates a map entry for the given message template variable with the name of the variable as key.
     *
     * @param variableAppender the message template variable
     * @return the map entry
     */
    private static Entry<String, VariableAppender<ActionLogger>> createEntry(
        VariableAppender<ActionLogger> variableAppender) {
        return new AbstractMap.SimpleEntry<>(variableAppender.getVariableName(), variableAppender);
    }

    private static String StackTraceToString(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }

    final private static String HOST_NAME_NOT_DETERMINABLE = "<HOST NAME NOT DETERMINABLE>";
    final private static String HOST_ADDRESS_NOT_DETERMINABLE = "<HOST ADDRESS NOT DETERMINABLE>";
    final private static String NO_CPU_TIME_AVAILABLE = "<NO CPU TIME AVAILABLE>";
    final private static String NO_THROWABLE_AVAILABLE = "<NO THROWABLE AVAILABLE>";
    final private static String NO_CAUSE_FOR_THROWABLE_AVAILABLE = "<NO CAUSE FOR THROWABLE AVAILABLE>";

    final public static Map<String, VariableAppender<ActionLogger>> DEFAULT_VARIABLE_APPENDERS = Map.ofEntries(

        // *********************************************************************************************************
        // *************************************** Step Progress Variables *****************************************
        // *********************************************************************************************************

        // StepNumber is the number of the step. StepNumber starts with 1.
        createEntry(new VariableAppender<>("StepNumber") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(
                    actionLogger.getUnitDecimalFormat().format(
                        actionLogger.getStepNumber()
                    )
                );
            }
        }),

        // StepNumber is the number of the step. StepNumber starts with 0.
        createEntry(new VariableAppender<>("StepIndex") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(
                    actionLogger.getUnitDecimalFormat().format(
                        actionLogger.getStepNumber() - 1
                    )
                );
            }
        }),

        createEntry(new VariableAppender<>("StepProcessedUnits") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(
                    actionLogger.getUnitDecimalFormat().format(
                        actionLogger.getStepUnits()
                    )
                );
            }
        }),

        createEntry(new VariableAppender<>("StepProcessingTime") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(
                    actionLogger.getDurationFormatter().format(
                        actionLogger.getStepStopWatch().getLastRecordedTime()
                    )
                );
            }
        }),

        // *********************************************************************************************************
        // *************************************** Batch Progress Variables ****************************************
        // *********************************************************************************************************

        // BatchStepNumber is the step number within the current batch. BatchStepNumber starts with 1.
        createEntry(new VariableAppender<>("BatchStepNumber") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(
                    actionLogger.getUnitDecimalFormat().format(
                        (actionLogger.getStepNumber() - 1) % actionLogger.getBatchSize() + 1
                    )
                );
            }
        }),

        // BatchStepIndex is the step index within the current batch. BatchStepIndex starts with 0.
        createEntry(new VariableAppender<>("BatchStepIndex") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(
                    actionLogger.getUnitDecimalFormat().format(
                        (actionLogger.getStepNumber() - 1) % actionLogger.getBatchSize() + 1
                    )
                );
            }
        }),

        // BatchFirstStepNumber is the minimum step number within the current batch, e.g. 1001
        createEntry(new VariableAppender<>("BatchMinStepNumber") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(
                    actionLogger.getUnitDecimalFormat().format(
                        (actionLogger.getStepNumber() - 1) / actionLogger.getBatchSize() * actionLogger.getBatchSize()
                            + 1
                    )
                );
            }
        }),

        // BatchFirstStepIndex is the minimum step index within the current batch, e.g. 1000
        createEntry(new VariableAppender<>("BatchMinStepIndex") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(
                    actionLogger.getUnitDecimalFormat().format(
                        (actionLogger.getStepNumber() - 1) / actionLogger.getBatchSize() * actionLogger.getBatchSize()
                    )
                );
            }
        }),

        // BatchMaxStepNumber is the maximum step number within the current batch, e.g. 2000
        createEntry(new VariableAppender<>("BatchMaxStepNumber") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(
                    actionLogger.getUnitDecimalFormat().format(
                        (actionLogger.getStepNumber() - 1)
                            / actionLogger.getBatchSize() * actionLogger.getBatchSize() + actionLogger.getBatchSize()
                    )
                );
            }
        }),

        // BatchMaxStepIndex is the maximum step index within the current batch, e.g. 1999
        createEntry(new VariableAppender<>("BatchMaxStepIndex") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(
                    actionLogger.getUnitDecimalFormat().format(
                        (actionLogger.getStepNumber() - 1)
                            / actionLogger.getBatchSize() * actionLogger.getBatchSize() + (actionLogger.getBatchSize()
                            - 1)
                    )
                );
            }
        }),

        // BatchNumber is the number of the current batch. BatchNumber starts with 1.
        createEntry(new VariableAppender<>("BatchNumber") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(
                    actionLogger.getUnitDecimalFormat().format(
                        (actionLogger.getStepNumber() - 1) / actionLogger.getBatchSize() + 1
                    )
                );
            }
        }),

        // BatchIndex is the index of the current batch. BatchIndex starts with 0.
        createEntry(new VariableAppender<>("BatchIndex") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(
                    actionLogger.getUnitDecimalFormat().format(
                        (actionLogger.getStepNumber() - 1) / actionLogger.getBatchSize()
                    )
                );
            }
        }),

        createEntry(new VariableAppender<>("BatchProcessedUnits") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(
                    actionLogger.getUnitDecimalFormat().format(
                        actionLogger.getBatchProgress().getProcessedUnits()
                    )
                );
            }
        }),

        createEntry(new VariableAppender<>("BatchProcessingTime") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(
                    actionLogger.getUnitDecimalFormat().format(
                        actionLogger.getBatchProgress().getProcessingTime()
                    )
                );
            }
        }),

        createEntry(new VariableAppender<>("BatchThroughputUnits") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(
                    actionLogger.getUnitDecimalFormat().format(
                        actionLogger.getBatchProgress().getProcessedUnitsPer(
                            1, actionLogger.getThroughputTimeUnit()
                        )
                    )
                );
            }
        }),

        createEntry(new VariableAppender<>("BatchThroughputTimeUnit") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(
                    actionLogger.getUnitDecimalFormat().format(
                        actionLogger.getBatchProgress().getProcessedUnitsPer(
                            1, actionLogger.getThroughputTimeUnit()
                        )
                    )
                );
            }
        }),

        // *********************************************************************************************************
        // *************************************** Total Progress Variables ****************************************
        // *********************************************************************************************************

        createEntry(new VariableAppender<>("TotalProcessedUnits") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(
                    actionLogger.getUnitDecimalFormat().format(actionLogger.getTotalProgress().getProcessedUnits())
                );
            }
        }),

        createEntry(new VariableAppender<>("TotalProcessingTime") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(
                    actionLogger.getUnitDecimalFormat().format(
                        actionLogger.getTotalProgress().getProcessingTime()
                    )
                );
            }
        }),

        createEntry(new VariableAppender<>("TotalThroughputUnits") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(
                    actionLogger.getUnitDecimalFormat().format(
                        actionLogger.getTotalProgress().getProcessedUnitsPer(
                            1, actionLogger.getThroughputTimeUnit()
                        )
                    )
                );
            }
        }),

        // *********************************************************************************************************
        // ************************************* Throwable Progress Variables **************************************
        // *********************************************************************************************************

        createEntry(new VariableAppender<>("ThrowableMessage") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(
                    actionLogger.getThrowable() == null ?
                        NO_THROWABLE_AVAILABLE :
                        actionLogger.getThrowable().getMessage()
                );
            }
        }),

        createEntry(new VariableAppender<>("ThrowableCause") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(
                    actionLogger.getThrowable() == null ?
                        NO_THROWABLE_AVAILABLE :
                        actionLogger.getThrowable().getCause() == null ?
                            NO_CAUSE_FOR_THROWABLE_AVAILABLE :
                            actionLogger.getThrowable().getCause().getMessage()
                );
            }
        }),

        createEntry(new VariableAppender<>("ThrowableStackTrace") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(
                    actionLogger.getThrowable() == null ?
                        NO_THROWABLE_AVAILABLE :
                        StackTraceToString(actionLogger.getThrowable())
                );
            }
        }),

        // *********************************************************************************************************
        // *************************************** Other Progress Variables ****************************************
        // *********************************************************************************************************

        createEntry(new VariableAppender<>("ThroughputTimeUnit") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(actionLogger.getThroughputTimeUnit());
            }
        }),

        // *********************************************************************************************************
        // **************************************** Time-related Variables *****************************************
        // *********************************************************************************************************

        createEntry(new VariableAppender<>("TotalTime") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(
                    actionLogger.getDurationFormatter().format(
                        actionLogger.getStepStopWatch().getTotalTime()
                    )
                );
            }
        }),

        createEntry(new VariableAppender<>("CurrentDateTime") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(actionLogger.getDateTimeFormatter().format(LocalDateTime.now()));
            }
        }),

        createEntry(new VariableAppender<>("CurrentTime") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(actionLogger.getTimeFormatter().format(LocalTime.now()));
            }
        }),

        createEntry(new VariableAppender<>("CurrentDate") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(actionLogger.getDateFormatter().format(LocalDate.now()));
            }
        }),

        // *********************************************************************************************************
        // ************************************* Environment-related Variables *************************************
        // *********************************************************************************************************

        createEntry(new VariableAppender<>("Hostname") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                try {
                    stringBuilder.append(InetAddress.getLocalHost().getHostName());
                } catch (UnknownHostException e) {
                    stringBuilder.append(HOST_NAME_NOT_DETERMINABLE);
                }
            }
        }),

        // default ip address
        // does no work for macOS, see https://www.baeldung.com/java-get-ip-address
        createEntry(new VariableAppender<>("HostAddress") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                try (final DatagramSocket datagramSocket = new DatagramSocket()) {
                    // google dns must not be reachable (only validity of the address format is checked)
                    datagramSocket.connect(InetAddress.getByName("8.8.8.8"), 12345);
                    stringBuilder.append(datagramSocket.getLocalAddress().getHostAddress());
                } catch (SocketException | UnknownHostException ignore) {
                    stringBuilder.append(HOST_ADDRESS_NOT_DETERMINABLE);
                }
            }
        }),

        createEntry(new VariableAppender<>("ProcessId") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(ProcessHandle.current().pid());
            }
        }),

        createEntry(new VariableAppender<>("ProcessCpuTime") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(
                    ProcessHandle.current().info().totalCpuDuration().isEmpty() ?
                        NO_CPU_TIME_AVAILABLE :
                        actionLogger.getDurationFormatter()
                            .format(ProcessHandle.current().info().totalCpuDuration().get())
                );
            }
        }),

        createEntry(new VariableAppender<>("CpuCores") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(Runtime.getRuntime().availableProcessors());
            }
        }),

        // *********************************************************************************************************
        // ************************************* System Properties Variables ***************************************
        // *********************************************************************************************************

        createEntry(new VariableAppender<>("Username") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(System.getProperty("user.name"));
            }
        }),

        createEntry(new VariableAppender<>("OsName") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(System.getProperty("os.name"));
            }
        }),

        createEntry(new VariableAppender<>("OsVersion") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(System.getProperty("os.version"));
            }
        }),

        createEntry(new VariableAppender<>("OsArchitecture") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(System.getProperty("os.arch"));
            }
        }),

        createEntry(new VariableAppender<>("JavaVendor") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(System.getProperty("java.vendor"));
            }
        }),

        createEntry(new VariableAppender<>("JavaVersion") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(System.getProperty("java.version"));
            }
        }),

        createEntry(new VariableAppender<>("JavaVmName") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(System.getProperty("java.vm.name"));
            }
        }),

        createEntry(new VariableAppender<>("JavaVmVendor") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(System.getProperty("java.vendor"));
            }
        }),

        createEntry(new VariableAppender<>("JavaVmVersion") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(System.getProperty("java.version"));
            }
        }),

        createEntry(new VariableAppender<>("JavaSpecificationName") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(System.getProperty("java.specification.name"));
            }
        }),

        createEntry(new VariableAppender<>("JavaSpecificationVendor") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(System.getProperty("java.specification.vendor"));
            }
        }),

        createEntry(new VariableAppender<>("JavaSpecificationVersion") {
            @Override
            public void append(ActionLogger actionLogger, StringBuilder stringBuilder) {
                stringBuilder.append(System.getProperty("java.specification.version"));
            }
        })
    );

}
