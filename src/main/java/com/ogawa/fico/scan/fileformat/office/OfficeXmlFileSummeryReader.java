package com.ogawa.fico.scan.fileformat.office;

import com.ogawa.fico.service.FileNaming;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.apache.poi.hpsf.PropertySet;
import org.apache.poi.hpsf.PropertySetFactory;
import org.apache.poi.poifs.eventfilesystem.POIFSReader;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderEvent;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderListener;

public class OfficeXmlFileSummeryReader {

    private final static List<String> supportedBinaryFileTypes = Arrays.asList(
        "doc", "dot",
        "xls", "ppt", "vsd", "accdb", "mdb"
    );

    @SuppressWarnings("SpellCheckingInspection")
    private final static List<String> supportedXmlFileTypes = Arrays.asList(
        "docx", "docm",
        "xlsx", "xlsm",
        "pptx", "pptm",
        "vsdx", "pub"
    );

    static class SummaryInformationListener<P extends PropertySet> implements POIFSReaderListener {

        P summaryInformation = null;

        public void processPOIFSReaderEvent(POIFSReaderEvent event) {
            try {
                summaryInformation = (P) PropertySetFactory.create(event.getStream());
            } catch (Exception ex) {
                throw new RuntimeException
                    ("Property set stream \"" + event.getPath() + event.getName() + "\": " + ex);
            }
        }

    }

    static private <P extends PropertySet> P readInfo(String filename, String name,
        SummaryInformationListener<P> listener) {

        POIFSReader r = new POIFSReader();

        try (FileInputStream fileInputStream = new FileInputStream(filename)) {
            r.registerListener(listener, name);
            r.read(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();

        }

        return listener.summaryInformation;
    }

    static public OfficeFileSummaryInfo readSummaryInfo(String filename) {
        SummaryInformationListener<org.apache.poi.hpsf.SummaryInformation> listener = new SummaryInformationListener<>();
        return new OfficeFileSummaryInfo(readInfo(filename, "\005SummaryInformation", listener));
    }

    static public OfficeFileSummaryInfo readSummaryInfo(Path filename) {
        return readSummaryInfo(filename.toString());
    }

    static public OfficeFileDocSummaryInfo readDocSummaryInfo(String filename) {
        SummaryInformationListener<org.apache.poi.hpsf.DocumentSummaryInformation> listener = new SummaryInformationListener<>();
        return new OfficeFileDocSummaryInfo(readInfo(filename, "\005DocumentSummaryInformation", listener));
    }

    static public OfficeFileDocSummaryInfo readDocSummaryInfo(Path filename) {
        return readDocSummaryInfo(filename.toString());
    }

    public static boolean isSupportedFileType(Path filename) {
        String fileExtension = FileNaming.getFilenameExtension(filename.toString()).toLowerCase();
        return supportedXmlFileTypes.contains(fileExtension) || supportedBinaryFileTypes.contains(fileExtension);
    }

}
