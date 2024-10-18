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
            toLongValue(row[6]),
            toLongValue(row[0]),
            toStringValue(row[1]),
            toStringValue(row[2]),
            toStringValue(row[3]),
            toLocalDateTime((java.sql.Timestamp) row[4]),
            toLocalDateTime((java.sql.Timestamp) row[5])
        );
        return scanBean;
    }

    @Override
    public Object[] toRow(ScanBean scanBean) {
        return new Object[]{
            scanBean.getProcessId(),
            scanBean.getHostName(),
            scanBean.getUserName(),
            scanBean.getRoot(),
            scanBean.getStarted(),
            scanBean.getFinished(),
            scanBean.getScanId()
        };

    }

    @Override
    public Object[] getPrimaryKeyValues(ScanBean object) {
        if (object.getScanId() == null) {
            return null;
        } else {
            return new Object[]{object.getScanId()};
        }
    }

    @Override
    public void setPrimaryKeyValues(ScanBean object, Object[] primaryKeyValues) {
        object.setScanId(toLongValue(primaryKeyValues[0]));
    }

    @Override
    public boolean hasGeneratedKeys() {
        return true;
    }
}
