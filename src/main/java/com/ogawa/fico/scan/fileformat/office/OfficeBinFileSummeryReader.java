package com.ogawa.fico.scan.fileformat.office;

import com.ogawa.fico.service.FileNaming;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.PropertySet;
import org.apache.poi.hpsf.PropertySetFactory;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.poifs.eventfilesystem.POIFSReader;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderEvent;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderListener;
import org.apache.poi.poifs.filesystem.NotOLE2FileException;

@SuppressWarnings("SpellCheckingInspection")
public class OfficeBinFileSummeryReader {

    private final static List<String> supportedBinaryFileTypes = Arrays.asList(
        "doc", "dot",
        "xls", "ppt", "vsd", "accdb", "mdb"
    );

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
        } catch (NotOLE2FileException notOLE2FileException) {
            return null;
        } catch (IOException e) {
            throw new RuntimeException("Error reading file " + filename, e);
        }

        return listener.summaryInformation;
    }

    static public OfficeFileSummaryInfo readSummaryInfo(String filename) {
        SummaryInformationListener<SummaryInformation> listener = new SummaryInformationListener<>();
        SummaryInformation summary = readInfo(filename, "\005SummaryInformation", listener);
        if (summary == null) {
            return null;
        } else {
            return new OfficeFileSummaryInfo(summary);
        }
    }

    static public OfficeFileSummaryInfo readSummaryInfo(Path filename) {
        return readSummaryInfo(filename.toString());
    }

    static public OfficeFileDocSummaryInfo readDocSummaryInfo(String filename) {
        SummaryInformationListener<DocumentSummaryInformation> listener = new SummaryInformationListener<>();
        DocumentSummaryInformation docSummary = readInfo(filename, "\005DocumentSummaryInformation", listener);
        if (docSummary == null) {
            return null;
        } else {
            return new OfficeFileDocSummaryInfo(docSummary);
        }
    }

    static public OfficeFileDocSummaryInfo readDocSummaryInfo(Path filename) {
        return readDocSummaryInfo(filename.toString());
    }

    public static boolean isSupportedFileType(Path filename) {
        String fileExtension = FileNaming.getFilenameExtension(filename.toString()).toLowerCase();
        return supportedXmlFileTypes.contains(fileExtension) || supportedBinaryFileTypes.contains(fileExtension);
    }

}
