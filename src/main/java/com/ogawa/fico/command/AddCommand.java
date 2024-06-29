package com.ogawa.fico.command;

import com.ogawa.fico.PersistingFileVisitor;
import com.ogawa.fico.command.argument.CommandWithAtLeastOneArg;
import com.ogawa.fico.db.FileIdSequenceFactory;
import com.ogawa.fico.db.FileRowCreator;
import com.ogawa.fico.db.ScanRowWriter;
import com.ogawa.fico.db.Sequence;
import com.ogawa.fico.exception.CommandLineError;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

// TODO: iterate over all paths given
public class AddCommand extends DatabaseCommand implements CommandWithAtLeastOneArg {

    static final public String KEY_WORD = "add";

    AddCommand(String[] commandArguments) {
        super(commandArguments);
        checkPaths();
    }

    @Override
    public String getName() {
        return KEY_WORD;
    }

    private Path getRootPath() {
        return Path.of(getArgument(0));
    }

    private void checkPaths() {
        for (int i = 0; i < getArgumentCount() - 1; i++) {
            String path = getArgument(i);
            if (!Files.exists(Path.of(path))) {
                throw new CommandLineError("File, dir or UNC not found or invalid: " + path);
            }
        }
    }

    public void execute() {

        ScanRowWriter scanRowWriter = new ScanRowWriter(getConnection());

        long scanId = scanRowWriter.create(getRootPath());

        Sequence fileIdSequence = FileIdSequenceFactory.getFileIdSequence(getConnection(), scanId);

        FileRowCreator fileRowCreator = new FileRowCreator(getConnection());

        PersistingFileVisitor fileVisitor = new PersistingFileVisitor(
            scanId, getRootPath(), fileIdSequence, fileRowCreator
        );

        scanRowWriter.updateStarted(scanId, new Date());

        try {
            fileVisitor.walk();
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }

        fileRowCreator.close();
        scanRowWriter.updateFinished(scanId, new Date());

        System.out.print("Files " + fileVisitor.getFileCount());
        System.out.print(" from " + fileVisitor.getDirCount());
        System.out.println(" directories added to " + getDatabaseName());

    }

}
