package com.ogawa.fico.command;

import static com.ogawa.fico.application.Config.getDefaultServerPort;
import static com.ogawa.fico.application.Config.getServerShutdownPassword;

import com.ogawa.fico.application.Application;
import com.ogawa.fico.command.argument.CommandWithOptionalTwoArgs;
import com.ogawa.fico.exception.InvalidCommandArgumentNumber;
import com.ogawa.fico.exception.ServerError;
import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;

@Slf4j
public class StopCommand extends ServerCommand implements CommandWithOptionalTwoArgs {

    static final public String KEY_WORD = "stop";

    public StopCommand(String[] commandArguments) {
        super(commandArguments);
    }

    @Override
    public String getName() {
        return KEY_WORD;
    }

    private boolean isValidPortNumber(String port) {
        try {
            int portNumber = Integer.parseInt(port);
            return portNumber > 0 && portNumber < 65536;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    void execute() {

        String port;

        // If no port is provided, use the default port
        if (getArgumentCount() == 0) {
            port = "" + getDefaultServerPort();
        } else {
            String tcpPort = getArgument(0);
            if (!tcpPort.equals("-tcpPort")) {
                throw new InvalidCommandArgumentNumber("Invalid argument: " + tcpPort);
            }
            port = getArgument(1);
            if (!isValidPortNumber(port)) {
                throw new InvalidCommandArgumentNumber("Invalid port number: " + port);
            }
        }

        try {
            log.info("Requesting shutdown of " + Application.getName() + " server");
            Server.shutdownTcpServer("tcp://localhost:" + port, getServerShutdownPassword(), true, true);
        } catch (Exception exception) {
            throw new ServerError("Could not stop server: " + exception.getMessage());
        }
    }
}
