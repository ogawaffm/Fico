package com.ogawa.fico.scan;

import com.ogawa.fico.db.Sequence;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.NonNull;

public class ScanBeanFactory {

    static public ScanBean create(Long scanId, Long processId, String hostName, String userName, String root,
        LocalDateTime started, LocalDateTime finished) {

        ScanBean scanBean = new ScanBean();
        scanBean.scanId = scanId;
        scanBean.processId = processId;
        scanBean.hostName = hostName;
        scanBean.userName = userName;
        scanBean.root = root;
        scanBean.started = started;
        scanBean.finished = finished;

        return scanBean;
    }

}
