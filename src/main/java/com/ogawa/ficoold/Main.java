package com.ogawa.ficoold;

import com.ogawa.ficoold.checksum.CheckSummer;
import com.ogawa.ficoold.db.DbFilePersister;
import com.ogawa.ficoold.db.FilePersister;
import com.ogawa.ficoold.db.FileRowWriter;
import com.ogawa.ficoold.db.Model;

import com.ogawa.ficoold.db.ScanRowWriter;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;

public class Main {

    private static void help(boolean error) {
        StringBuilder sb = new StringBuilder();
        sb.append("FiCo 1.0\n");
        if (error) {
            sb.append("\ncommandline error.\n\ninvoke:\n");
        }
        sb.append("\n");
        sb.append("FiCo DatabaseName RootPath");
        sb.append("\n");
        sb.append("    with \n");
        sb.append("        DatabaseName - name of the h2 database to connect to logEvent localhost\n");
        sb.append("        RootPath - root path to walk scanning from\n");
        sb.append("\n");
        sb.append("    for example \n");
        sb.append("        Fico files c:\\\n");
        sb.append("        fico files \\MYNAS\\SHARENAME\n");

        if (error) {
            System.err.println(sb);
        } else {
            System.out.println(sb);
        }
    }

    public static void main(String[] args) {

        Date start = new Date();

        System.out.println("Started at " + start);

        if (args.length != 2) {
            help(args.length != 0);
            System.exit(args.length == 0 ? 0 : 1);
        }

        String databaseName = args[0];
        Path rootPath = Path.of(args[1]);
        int fileBufferSize = 64 * 1024 * 1024;

        Connection connection;

        try {
            connection =
                DriverManager.getConnection("jdbc:h2:mem:~/" + databaseName, "sa", "");
            //DriverManager.getConnection("jdbc:h2:tcp://localhost/~/" + databaseName, "sa", "");
/*            connection =
DriverManager.getConnection("jdbc:h2:tcp://localhost/~/" + databaseName, "sa", "");
                DriverManager.getConnection("jdbc:h2:~/" + databaseName, "sa", "");
*/
        } catch (SQLException sqlException) {
            throw new RuntimeException(
                "Cannot connect to local h2 database '" + databaseName + "'. Launch H2 and restart FiCo", sqlException);
        }

        int fileCount = 0;

        try {

            Model model = new Model(connection);

            model.createModel();

            ScanRowWriter scanRowWriter = new ScanRowWriter(connection);

            int scanId = scanRowWriter.create(rootPath);

            FileRowWriter fileRowWriter = new FileRowWriter(connection, scanId);

            FilePersister<Integer> filePersister = new DbFilePersister(fileRowWriter);

            PersistingFileVisitor fileVisitor = new PersistingFileVisitor(rootPath, filePersister);

            scanRowWriter.updateStarted(scanId, new Date());
            fileVisitor.walk();
            scanRowWriter.updateFinished(scanId, new Date());

            CheckSummer checkSummer = new CheckSummer();
            fileCount = checkSummer.sum(connection);

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        try {
            connection.close();
        } catch (SQLException sqlException) {
            throw new RuntimeException("Could not close local h2 database '" + databaseName + "'.", sqlException);
        }

        System.out.println(
            "Finished at " + new Date() + ". Calculated checksums for " + fileCount + " files in "
                + (new Date().getTime() - start.getTime()) / 1000 + " s"
        );

    }

}
