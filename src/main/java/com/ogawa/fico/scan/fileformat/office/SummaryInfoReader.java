package com.ogawa.fico.scan.fileformat.office;

import org.apache.poi.hpsf.SummaryInformation;

public class SummaryInfoReader extends AbstractSummeryInfoReader<SummaryInformation, OfficeFileSummaryInfo> {

    public OfficeFileSummaryInfo read(String filename) {
        return new OfficeFileSummaryInfo(readInfo(filename, "\005SummaryInformation"));
    }

}
