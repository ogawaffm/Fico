package com.ogawa.fico.application;

import com.ogawa.fico.PersistingFileVisitor;
import com.ogawa.fico.command.Command;
import com.ogawa.fico.exception.CommandLineError;
import com.ogawa.fico.command.CommandLineParser;
import com.ogawa.fico.db.FileRowCreator;
import com.ogawa.fico.db.Model;
import com.ogawa.fico.db.ScanRowWriter;
import com.ogawa.fico.db.Util;
import com.ogawa.fico.exception.ExecutionException;
import com.ogawa.fico.exception.ModelError;
import com.ogawa.fico.performance.logging.DurationFormatter;
import com.ogawa.fico.performance.measuring.StopWatch;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

public class Main {

    static private void printException(Exception exception) {
        // internal error?
        if (exception.getCause() != null) {
            System.err.println(exception.getMessage() + " " + exception.getCause().getMessage());
        } else {
            // no, print only message
            System.err.println(exception.getMessage());
        }
    }

    public static void main(String[] args) {

        try {

            Command command = CommandLineParser.parse(args);
            command.run();

        } catch (ExecutionException executionException) {
            printException(executionException);
            System.exit(1);

        } catch (ModelError modelError) {
            printException(modelError);
            System.exit(1);

        } catch (CommandLineError commandLineError) {
            printException(commandLineError);
            System.exit(1);

        } catch (Exception exception) {
            exception.printStackTrace();
            System.exit(1);
        }

    }

}
