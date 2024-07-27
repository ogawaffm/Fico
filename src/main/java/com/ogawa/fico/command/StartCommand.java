package com.ogawa.fico.command;

import static com.ogawa.fico.application.Config.getServerShutdownPassword;

import com.ogawa.fico.command.argument.CommandWithNoOrMoreArgs;
import com.ogawa.fico.db.Util;
import com.ogawa.fico.exception.InvalidCommandArgument;
import com.ogawa.fico.exception.ServerError;
import java.sql.Connection;
import java.sql.SQLFeatureNotSupportedException;
import org.h2.jdbc.JdbcSQLFeatureNotSupportedException;
import org.h2.server.TcpServer;
import org.h2.tools.Console;
import org.h2.tools.Server;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StartCommand extends ServerCommand implements CommandWithNoOrMoreArgs {

    static final public String KEY_WORD = "start";

    Server tcpServer;

    public StartCommand(String[] commandArguments) {
        super(commandArguments);
    }

    @Override
    public String getName() {
        return KEY_WORD;
    }

    String[] concatArgs(String[] args1, String[] args2) {
        String[] result = new String[args1.length + args2.length];
        System.arraycopy(args1, 0, result, 0, args1.length);
        System.arraycopy(args2, 0, result, args1.length, args2.length);
        return result;
    }

    @Override
    void beforeExecute() {
    }

    @Override
    public void afterExecute() {
    }

    @Override
    void execute() {
        try {
            String[] args = concatArgs(
                new String[]{"-tcp", "-tcpPassword", getServerShutdownPassword()},
                this.getArguments());

            tcpServer = Server.createTcpServer(args);
            tcpServer.start();
            log.info(tcpServer.getStatus());
            tcpServer.run();

        } catch (JdbcSQLFeatureNotSupportedException exception) {
            throw new InvalidCommandArgument(exception.getMessage(), exception);

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new ServerError(exception);
        }
    }
}
