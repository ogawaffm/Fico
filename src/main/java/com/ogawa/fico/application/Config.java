package com.ogawa.fico.application;

import java.nio.file.Path;

public class Config {

    private final String databaseName;
    private final Path rootPath;

    public Config(String[] args) {
        checkAndAnalyse(args);
        databaseName = args[0];
        rootPath = Path.of(args[1]);
    }

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
        sb.append("        fico files \\\\MYNAS\\SHARENAME\n");

        if (error) {
            System.err.println(sb);
        } else {
            System.out.println(sb);
        }
    }

    private void checkAndAnalyse(String[] args) {
        if (args.length != 2) {
            help(true);
            System.exit(1);
        }
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public Path getRootPath() {
        return rootPath;
    }


}
