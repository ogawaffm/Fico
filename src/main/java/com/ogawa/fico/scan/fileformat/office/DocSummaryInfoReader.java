package com.ogawa.fico.scan.fileformat.office;

import org.apache.poi.hpsf.DocumentSummaryInformation;

public class DocSummaryInfoReader extends
    AbstractSummeryInfoReader<DocumentSummaryInformation, OfficeFileDocSummaryInfo> {

    public OfficeFileDocSummaryInfo read(String filename) {
        return new OfficeFileDocSummaryInfo(readInfo(filename, "\005DocumentSummaryInformation"));
    }

}
