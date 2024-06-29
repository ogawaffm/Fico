package com.ogawa.fico.application;

import com.ogawa.fico.command.CommandLineParser;
import com.ogawa.fico.db.Util;
import com.ogawa.fico.exception.CommandLineError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class Config {

    static private String databaseName;

    static public String getDefaultDatabaseName() {
        if (System.getenv("FICO_DB") != null) {
            return System.getenv("FICO_DB");
        } else {
            return "fico";
        }
    }

    static public void setDatabaseName(String databaseName) {
        Config.databaseName = databaseName;
    }

    static public String getDatabaseName() {
        if (databaseName == null) {
            return getDefaultDatabaseName();
        } else {
            return databaseName;
        }
    }

    static public boolean isDatabaseSetByArgument() {
        return databaseName != null;
    }

    public static String getResource(String filename) {

        int bufferSize = 8192;
        char[] buffer = new char[bufferSize];

        StringBuilder stringBuilder = new StringBuilder();

        InputStream inputStream = Util.class.getClassLoader().getResourceAsStream(filename);

        if (inputStream == null) {
            throw new RuntimeException("Could not find file " + filename);
        }

        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

            for (int numRead; (numRead = reader.read(buffer, 0, buffer.length)) > 0; ) {
                stringBuilder.append(buffer, 0, numRead);
            }

        } catch (IOException ioException) {
            throw new RuntimeException("Could not read file " + filename, ioException);
        } finally {
            try {
                inputStream.close();
            } catch (IOException ioException) {
                throw new RuntimeException("Could not close file " + filename, ioException);
            }
        }

        return stringBuilder.toString();

    }

}
