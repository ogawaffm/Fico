package com.ogawa.fico.db.persistence.rowmapper;

import com.ogawa.fico.scan.ScanBean;
import com.ogawa.fico.scan.ScanBeanFactory;
import java.util.List;

import static com.ogawa.fico.db.persistence.rowmapper.RowCaster.*;

public class ScanRowMapper implements RowMapper<ScanBean> {

    private static final List<String> COLUMN_NAMES = List.of(
        "SCAN_ID",
        "PROCESS_ID",
        "HOST_NAME",
        "USER_NAME",
        "ROOT",
        "STARTED",
        "FINISHED"
    );

    @Override
    public List<String> getColumnNames() {
        return COLUMN_NAMES;
    }

    public List<String> getPrimaryKeyColumnNames() {
        return List.of("SCAN_ID");
    }

    @Override
    public ScanBean toObject(Object[] row) {
        ScanBean scanBean = ScanBeanFactory.create(
            toLongValue(row[0]),
            toLongValue(row[1]),
            toStringValue(row[2]),
            toStringValue(row[3]),
            toStringValue(row[4]),
            toLocalDateTime((java.sql.Timestamp) row[5]),
            toLocalDateTime((java.sql.Timestamp) row[6])
        );
        return scanBean;
    }

    @Override
    public Object[] toRow(ScanBean scanBean) {
        return new Object[]{
            scanBean.getScanId(),
            scanBean.getProcessId(),
            scanBean.getHostName(),
            scanBean.getUserName(),
            scanBean.getRoot(),
            scanBean.getStarted(),
            scanBean.getFinished()
        };

    }

    @Override
    public Object[] getPrimaryKeyValues(ScanBean object) {
        return new Object[]{object.getScanId()};
    }

    @Override
    public void setPrimaryKeyValues(ScanBean object, Object[] primaryKeyValues) {
        object.setScanId(toLongValue(primaryKeyValues[0]));
    }
}
