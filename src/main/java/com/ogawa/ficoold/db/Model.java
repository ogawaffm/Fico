package com.ogawa.ficoold.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Model {

    private final Connection connection;

    public Model(Connection connection) {
        this.connection = connection;
    }

    private boolean tableExists(String tableName) {
        try (Statement statement = connection.createStatement();) {
            statement.execute("SELECT * FROM \"" + tableName + "\"\n WHERE 1 = 2");
            statement.close();
            return true;
        } catch (SQLException ignore) {
            return false;
        }
    }

    static private final String CREATE_MODEL =
        "" +
            "DROP VIEW SCAN_STAT IF EXISTS\n" +
            "\n" +
            "DROP TABLE FILE IF EXISTS\n" +
            "\n" +
            "DROP TABLE SCAN IF EXISTS\n" +
            "\n" +
            "CREATE TABLE SCAN (\n" +
            "    SCAN_ID       INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,\n" +
            "    ROOT          VARCHAR(512) NOT NULL,\n" +
            "    STARTED       TIMESTAMP,\n" +
            "    FINISHED      TIMESTAMP\n" +
            ")\n" +
            "\n" +

            "CREATE TABLE FILE (\n" +
            "    FILE_ID           INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,\n" +
            "    DIR_ID            INT,\n" +
            "    SCAN_ID           INT NULL,\n" +
            "    PATH              VARCHAR(512) NOT NULL,\n" +
            "    NAME              VARCHAR(512) NOT NULL,\n" +
            "    SIZE              BIGINT NOT NULL,\n" +
            "    LAST_WRITE_ACCESS TIMESTAMP NOT NULL,\n" +
            "    CHECKSUM          VARBINARY(32) DEFAULT X'0000000000000000000000000000000000000000000000000000000000000000',\n"
            +
            "    CALC_STARTED      TIMESTAMP,\n" +
            "    CALC_FINISHED     TIMESTAMP\n" +
            ")\n" +
            "\n" +
  /*          "ALTER TABLE FILE ADD FOREIGN KEY (DIR_ID) REFERENCES FILE (FILE_ID) ON DELETE CASCADE\n" +
            "\n" +
*/            "ALTER TABLE FILE ADD FOREIGN KEY (SCAN_ID) REFERENCES SCAN (SCAN_ID) ON DELETE CASCADE\n" +
            "\n" +
            "CREATE UNIQUE INDEX SCAN_ID_DIR_ID_NAME ON FILE (SCAN_ID, DIR_ID, NAME)\n" +
            "\n" +
            "CREATE INDEX NAME ON FILE (NAME)\n" +
            "\n" +
            "CREATE INDEX SIZE ON FILE (SIZE)\n" +
            "\n" +
            "CREATE INDEX LAST_WRITE_ACCESS ON FILE (LAST_WRITE_ACCESS)\n" +
            "\n" +
            "CREATE INDEX CHECKSUM ON FILE (CHECKSUM)\n" +
            "\n" +
            "CREATE INDEX SIZE_NAME ON FILE (SIZE, NAME)\n" +
            "\n" +
            "CREATE INDEX EQUAL ON FILE (SIZE, NAME, CHECKSUM)\n" +
            "\n" +
            "CREATE VIEW SCAN_STAT AS (\n" +
            "    SELECT SCAN.SCAN_ID SCAN_ID, ROOT, STARTED, FINISHED,\n" +
            "    SUM(CASE WHEN SIZE = -1 THEN NULL ELSE SIZE END) AS SIZE,\n" +
            "        SUM(CASE WHEN SIZE = -1 THEN 1 ELSE 0 END) AS DIR_COUNT,\n" +
            "        SUM(CASE WHEN SIZE = -1 THEN 0 ELSE 1 END) AS FILE_COUNT,\n" +
            "        MIN(LAST_WRITE_ACCESS) AS MIN_LAST_WRITE_ACCESS, \n" +
            "        MAX(LAST_WRITE_ACCESS) AS MAX_LAST_WRITE_ACCESS\n" +
            "    FROM SCAN\n" +
            "    JOIN FILE\n" +
            "        ON (SCAN.SCAN_ID = FILE.SCAN_ID)\n" +
            "    GROUP BY SCAN.SCAN_ID, ROOT, STARTED, FINISHED\n" +
            ")\n" +
            "\n";


    public void createModel() throws SQLException {

        if (true || !tableExists("SCAN")) {

            Statement statement = connection.createStatement();

            String[] sqls = CREATE_MODEL.split("\\n\\n");

            for (String sql : sqls) {
                System.out.println(sql);
                statement.execute(sql);
            }

        }
    }

}
