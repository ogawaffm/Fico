package com.ogawa.fico.db.persistence.rowmapper;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class RowCaster {

    public static String toStringValue(Object object) {
        return object == null ? null : (String) object;
    }

    public static Long toLongValue(Object object) {
        return object == null ? null : ((Number) object).longValue();
    }

    public static Integer toIntegerValue(Object object) {
        return object == null ? null : ((Number) object).intValue();
    }

    public static Timestamp toTimestamp(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        } else {
            return Timestamp.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        }
    }

    public static LocalDateTime toLocalDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        } else {
            return timestamp.toLocalDateTime();
        }
    }

}
