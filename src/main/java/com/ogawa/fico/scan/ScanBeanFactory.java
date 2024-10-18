package com.ogawa.fico.scan;

import com.ogawa.fico.misc.System;
import java.time.LocalDateTime;

public class ScanBeanFactory {

    static public ScanBean create(String root) {
        return create(
            null,
            System.getPid(),
            System.getHostName(),
            System.getUsername(),
            root,
            null,
            null);
    }

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
