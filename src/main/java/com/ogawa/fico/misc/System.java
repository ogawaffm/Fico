package com.ogawa.fico.misc;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class System {

    public static PrintStream createPrintStream(String filename) throws IOException {
        if (isWindowsOs()) {
            return new PrintStream(filename, "windows-1252");
        } else {
            return new PrintStream(filename);
        }
    }

    /**
     * Returns the process id of the running jre
     *
     * @return
     */
    public static long getPid() {
        return ProcessHandle.current().pid();
    }

    /**
     * Returns the name of the host or "localhost" if a name is not determinable
     *
     * @return
     */
    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ignore) {
        }
        return "localhost";
    }

    /**
     * Returns the ip address  of the evaluation host machine or "" if not address is determinable
     *
     * @return ip address or ""
     */
    public static String getIPAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception ignore) {
            return "";
        }
    }

    /**
     * Returns a jre property
     *
     * @param propertyName
     * @return
     */
    public static String getProperty(String propertyName) {
        return java.lang.System.getProperty(propertyName);
    }

    /**
     * Returns the name of the current os user
     *
     * @return
     */
    public static String getUsername() {
        return getProperty("user.name");
    }

    /**
     * Returns an os environment variable
     *
     * @param variableName Name of the variable
     * @return value of the variable
     */
    public static String getEnvironmentVariable(String variableName) {
        return java.lang.System.getenv(variableName);
    }

    public static String getListDelimiter() {
        //TODO was never tested
        return Preferences.userRoot().get("\\Control Panel\\International", "sList");
    }

    /* ****************************************************************************** */
    /* ********************************* temp files ********************************* */
    /* ****************************************************************************** */

    private static String rtrim(String string) {
        return string.replaceAll("\\s+$", "");
    }

    /**
     * Returns the path to the passed class
     *
     * @param clazz Class to return path of
     * @return Returns the filename of the class, which is its .class or .jar file
     */
    @SuppressWarnings("rawtypes")
    public static Path getClassFilePath(Class clazz) {

        try {
            return Paths.get(clazz
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI());
        } catch (URISyntaxException uriSyntaxException) {
            throw new RuntimeException(uriSyntaxException);
        }
    }

    /**
     * Detects and returns whether this process was started from within intelliJ { @see:
     * https://intellij-support.jetbrains.com/hc/en-us/community/posts/360000015340-Detecting-Intellij-from-within-main-methods?page=1#community_comment_360000017399}
     *
     * @return true/false
     */
    public static boolean isRunningFromIntelliJ() {
        boolean runningInIntelliJ = false;
        try {
            runningInIntelliJ = System.class.getClassLoader().loadClass(
                "com.intellij.rt.execution.application.AppMainV2") != null;
        } catch (ClassNotFoundException ignore) {
        }
        return runningInIntelliJ;
    }

    /**
     * Returns whether the passed class is located in a jar-file
     *
     * @param clazz Class to check the location of
     * @return true/false
     */
    @SuppressWarnings("rawtypes")
    public static boolean isJared(Class clazz) {
        return getClassFilePath(clazz).toString().toLowerCase().endsWith(".jar");
    }

    /**
     * Determines if the operating system is Windows {@see org.apache.commons.lang3.SystemUtils}
     *
     * @return true/false
     */
    public static boolean isWindowsOs() {
        return java.lang.System.getProperty("os.name").startsWith("Windows");
    }

    /**
     * Returns the current dir
     *
     * @return Current dir
     */
    public static String getCurrentDir() {
        return java.lang.System.getProperty("user.dir");
    }

    /**
     * Determines whether the operating system is an unixoid OS like Linux or Mac (which is deemed to be the case if the
     * os is not Windows)
     *
     * @return true/false
     */
    public static boolean isUnixoidOs() {
        return !isWindowsOs();
    }

    /**
     * Returns the passed argument and sets it quotes, if it contains spaces to be equivalent to a command line
     * argument. E.g file.txt => file.txt; my file.txt => "my file.txt"
     *
     * @param arg unquoted command line argument value
     * @return arg as command line arg
     */
    public static String getAsCommandLineArg(String arg) {
        return arg.contains(" ") ? "\"" + arg + "\"" : arg;
    }

    /**
     * Returns the passed arguments concatenated and each quoted if necessary to be a valid command line argument
     *
     * @param args unquoted command line argument value array
     * @return All-over command line arguments string
     */
    public static String getAsCommandLineArgs(String[] args) {
        return Arrays.stream(args)
            .map(s -> getAsCommandLineArg(s))
            .collect(Collectors.joining(" "));
    }
}
