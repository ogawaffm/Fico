package com.ogawa.fico.command;

import com.ogawa.fico.command.argument.CommandWithAtLeastOneArg;
import com.ogawa.fico.exception.ExecutionError;
import com.ogawa.fico.scan.FileScanService;
import com.ogawa.fico.scan.FileScanner;
import com.ogawa.fico.scan.SimpleFileScanner;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

// TODO: iterate over all paths given
@Slf4j
public class OpenCommand extends DatabaseCommand implements CommandWithAtLeastOneArg {

    static final public String KEY_WORD = "open";

    OpenCommand(String[] commandArguments) {
        super(commandArguments);
        // assure all paths are valid or throw an exception
        checkPaths();
    }

    @Override
    public String getName() {
        return KEY_WORD;
    }

    /**
     * Get a unique set of root paths to scan. Duplicate paths will be ignored.
     *
     * @return Set of root paths
     */
    private Set<Path> getRootPathSet() {
        Map<Path, Path> rootPathMap = new LinkedHashMap<>();
        for (int i = 0; i < getArgumentCount(); i++) {
            Path rootPath = Path.of(getArgument(i));
            rootPathMap.put(rootPath.toAbsolutePath(), rootPath);
            if (rootPathMap.put(rootPath.toAbsolutePath(), rootPath) != null) {
                log.warn("Duplicate path will be ignored: " + rootPath);
            }
        }
        return new LinkedHashSet<>(rootPathMap.keySet());
    }

    private void throwNoSuchFileException(String path) {
        throw new ExecutionError("File, directory or UNC not found or invalid: " + path,
            new NoSuchFileException(path)
        );
    }

    /**
     * Check if all paths are valid. If not, an exception will be thrown.
     */
    private void checkPaths() {
        for (int i = 0; i < getArgumentCount(); i++) {
            String argument = getArgument(i);
            try {
                Path path = Path.of(argument);
                if (!Files.exists(path)) {
                    throwNoSuchFileException(argument);
                }
            } catch (InvalidPathException invalidPathException) {
                throwNoSuchFileException(argument);
            }
        }
    }

    public void execute() {
        try {
            for (String filename : getArguments()) {
                Desktop.getDesktop().open(new File(filename));
            }
        } catch (IOException e) {
            throwExecutionError(e);
        }
    }
}
