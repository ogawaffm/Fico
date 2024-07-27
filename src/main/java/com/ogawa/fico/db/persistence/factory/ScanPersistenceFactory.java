package com.ogawa.fico.db.persistence.factory;

import com.ogawa.fico.db.persistence.rowmapper.RowMapper;
import com.ogawa.fico.db.persistence.rowmapper.ScanRowMapper;
import com.ogawa.fico.scan.ScanBean;
import java.sql.Connection;

public class ScanPersistenceFactory extends PersistenceFactory<ScanBean> {

    static private final int DEFAULT_BATCH_SIZE = 1_000;

    static private final int DEFAULT_FETCH_SIZE = 1_000;

    public ScanPersistenceFactory(Connection connection) {
        this(connection, new ScanRowMapper());
    }

    protected ScanPersistenceFactory(Connection connection, RowMapper<ScanBean> rowMapper) {
        super(connection, rowMapper, "SCAN", DEFAULT_BATCH_SIZE, DEFAULT_FETCH_SIZE);
    }

}
