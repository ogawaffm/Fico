package com.ogawa.fico.scan;

import java.time.LocalDateTime;
import java.util.Comparator;
import lombok.Data;

@Data
public class ScanBean implements Comparable<ScanBean> {

    Long scanId;
    Long processId;
    String hostName;
    String userName;
    String root;
    LocalDateTime started;
    LocalDateTime finished;

    @Override
    public int compareTo(ScanBean o) {
        return Comparator.comparing(ScanBean::getHostName).thenComparing(ScanBean::getRoot).compare(this, o);
    }

}
