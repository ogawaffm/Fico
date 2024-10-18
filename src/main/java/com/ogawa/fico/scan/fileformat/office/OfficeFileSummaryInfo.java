package com.ogawa.fico.scan.fileformat.office;

import java.util.Date;
import lombok.Getter;
import lombok.ToString;
import org.apache.poi.hpsf.Filetime;

@ToString
@Getter
public class OfficeFileSummaryInfo {

    private final String applicationName;
    private final String title;
    private final String author;
    private final String keywords;
    private final String comments;
    private final String template;
    private final String lastAuthor;
    private final String revNumber;

    private final Date editTime;
    private final Date lastPrinted;
    private final Date createDateTime;
    private final Date lastSaveDateTime;

    private final byte[] thumbnail;
    private final Integer security;

    private final Integer pageCount;
    private final Integer wordCount;
    private final Integer charCount;

    public OfficeFileSummaryInfo(org.apache.poi.hpsf.SummaryInformation summaryInformation) {
        this.applicationName = summaryInformation.getApplicationName();
        this.title = summaryInformation.getTitle();
        this.author = summaryInformation.getAuthor();
        this.keywords = summaryInformation.getKeywords();
        this.comments = summaryInformation.getComments();
        this.template = summaryInformation.getTemplate();
        this.lastAuthor = summaryInformation.getLastAuthor();
        this.revNumber = summaryInformation.getRevNumber();

        this.editTime = Filetime.filetimeToDate(summaryInformation.getEditTime());
        this.lastPrinted = summaryInformation.getLastPrinted();
        this.createDateTime = summaryInformation.getCreateDateTime();
        this.lastSaveDateTime = summaryInformation.getLastSaveDateTime();

        this.thumbnail = summaryInformation.getThumbnail();
        this.security = summaryInformation.getSecurity();

        this.pageCount = summaryInformation.getPageCount();
        this.wordCount = summaryInformation.getWordCount();
        this.charCount = summaryInformation.getCharCount();

    }
}
