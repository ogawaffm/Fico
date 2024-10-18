package com.ogawa.fico.scan.fileformat.office;

import java.io.*;
import org.apache.poi.hpsf.*;
import org.apache.poi.poifs.eventfilesystem.*;

public abstract class AbstractSummeryInfoReader<P extends PropertySet, S> {

    static class SummaryInformationListener<P extends PropertySet> implements POIFSReaderListener {

        public P summaryInformation = null;

        public void processPOIFSReaderEvent(POIFSReaderEvent event) {
            try {
                summaryInformation = (P) PropertySetFactory.create(event.getStream());
            } catch (Exception ex) {
                throw new RuntimeException
                    ("Property set stream \"" + event.getPath() + event.getName() + "\": " + ex);
            }
        }

    }

    public P readInfo(String filename, String name) {

        POIFSReader r = new POIFSReader();
        SummaryInformationListener<P> listener = new SummaryInformationListener<>();

        try (FileInputStream fileInputStream = new FileInputStream(filename)) {
            r.registerListener(listener, "\005SummaryInformation");
            r.read(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();

        }

        return listener.summaryInformation;
    }

    public abstract S read(String filename);

}
