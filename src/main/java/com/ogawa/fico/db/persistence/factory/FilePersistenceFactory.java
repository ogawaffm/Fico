package com.ogawa.fico.db.persistence.factory;

import com.ogawa.fico.db.persistence.rowmapper.FileRowMapper;
import com.ogawa.fico.db.persistence.rowmapper.RowMapper;
import com.ogawa.fico.scan.FileBean;
import java.sql.Connection;

public class FilePersistenceFactory extends PersistenceFactory<FileBean> {

    static private final int DEFAULT_BATCH_SIZE = 1_000;

    static private final int DEFAULT_FETCH_SIZE = 1_000;

    public FilePersistenceFactory(Connection connection) {
        this(connection, new FileRowMapper());
    }

    FilePersistenceFactory(Connection connection, RowMapper<FileBean> rowMapper) {
        super(connection, rowMapper, "FILE", DEFAULT_BATCH_SIZE, DEFAULT_FETCH_SIZE);
    }

}
