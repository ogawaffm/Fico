package com.ogawa.fico.scan.fileformat.office;

import java.util.Date;
import org.apache.poi.ooxml.POIXMLProperties;

public class CoreProperties {

    private final String category;
    private final String contentStatus;
    private final String contentType;
    private final Date created;
    private final String creator;
    private final String description;
    private final String identifier;
    private final String keywords;
    private final String lastModifiedByUser;
    private final Date lastPrinted;
    private final Date modified;
    private final String revision;
    private final String subject;
    private final String title;
    private final String version;

    // PackagePropertiesPart UnderlyingProperties;

    CoreProperties(POIXMLProperties.CoreProperties coreProperties) {
        this.category = coreProperties.getCategory();
        this.contentStatus = coreProperties.getContentStatus();
        this.contentType = coreProperties.getContentType();
        this.created = coreProperties.getCreated();
        this.creator = coreProperties.getCreator();
        this.description = coreProperties.getDescription();
        this.identifier = coreProperties.getIdentifier();
        this.keywords = coreProperties.getKeywords();
        this.lastModifiedByUser = coreProperties.getLastModifiedByUser();
        this.lastPrinted = coreProperties.getLastPrinted();
        this.modified = coreProperties.getModified();
        this.revision = coreProperties.getRevision();
        this.subject = coreProperties.getSubject();
        this.title = coreProperties.getTitle();
        this.version = coreProperties.getVersion();
    }
}
