package com.ogawa.fico.scan.fileformat.office;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class OfficeFileDocSummaryInfo {

    private final String category;
    private final String presentationFormat;

    private final Integer byteCount;
    private final Integer lineCount;
    private final Integer paragraphCount;


    private final String manager;
    private final String company;

    private final Integer applicationVersion;
    private final Integer slideCount;
    private final Integer notes;
    private final Integer hiddenCount;
    private final Integer mmClipCount;
    private final Integer charCountWithSpaces;
    private final String contentStatus;
    private final String contentType;
    private final String documentVersion;
    private final String language;

    public OfficeFileDocSummaryInfo(org.apache.poi.hpsf.DocumentSummaryInformation documentSummaryInformation) {
        this.company = documentSummaryInformation.getCompany();
        this.manager = documentSummaryInformation.getManager();

        this.category = documentSummaryInformation.getCategory();
        this.presentationFormat = documentSummaryInformation.getPresentationFormat();

        this.byteCount = documentSummaryInformation.getByteCount();
        this.lineCount = documentSummaryInformation.getLineCount();
        this.paragraphCount = documentSummaryInformation.getParCount();

        this.slideCount = documentSummaryInformation.getSlideCount();
        this.notes = documentSummaryInformation.getNoteCount();
        this.hiddenCount = documentSummaryInformation.getHiddenCount();
        this.mmClipCount = documentSummaryInformation.getMMClipCount();

        this.applicationVersion = documentSummaryInformation.getApplicationVersion();

        this.charCountWithSpaces = documentSummaryInformation.getCharCountWithSpaces();

        this.contentStatus = documentSummaryInformation.getContentStatus();
        this.contentType = documentSummaryInformation.getContentType();
        this.documentVersion = documentSummaryInformation.getDocumentVersion();

        this.language = documentSummaryInformation.getLanguage();
    }

}
