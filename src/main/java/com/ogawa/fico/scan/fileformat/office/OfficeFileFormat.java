package com.ogawa.fico.scan.fileformat.office;

import lombok.Getter;

public enum OfficeFileFormat {

    UNKNOWN(OfficeApplication.UNKNOWN, "Unknown file format", "", false, false, false),

    /**
     * Word formats
     */
    DOC(OfficeApplication.WORD, "Document for Word (97-2003)", "doc", false, false, true),
    DOCX(OfficeApplication.WORD, "Document for Word (2007-)", "docx", true, true, false),
    DOCM(OfficeApplication.WORD, "Document for Word (2007-)", "docm", true, true, true),

    DOT(OfficeApplication.WORD, "Document template for Word (97-2003)", "dot", false, false, true),
    DOTX(OfficeApplication.WORD, "Document template for Word (2007-)", "dotx", true, true, false),
    DOTM(OfficeApplication.WORD, "Document template for Word (2007-)", "dotm", true, true, true),

    ODT(OfficeApplication.WORD, "OpenDocument Text", "odt", true, true, false),
    OTT(OfficeApplication.WORD, "OpenDocument Template", "ott", true, true, false),

    /**
     * Excel formats
     */
    XLS(OfficeApplication.EXCEL, "Workbook for Excel (-2003)", "xls", false, false, true),
    XLSB(OfficeApplication.EXCEL, "Workbook for Excel (2007-)", "xlsb", false, false, true),
    XLSX(OfficeApplication.EXCEL, "Workbook for Excel (2007-)", "xlsx", true, true, false),
    XLSM(OfficeApplication.EXCEL, "Workbook for Excel (2007-)", "xlsm", true, true, true),

    XLT(OfficeApplication.EXCEL, "Workbook template for Excel (97-2003)", "xlt", false, false, true),
    XLTX(OfficeApplication.EXCEL, "Workbook template for Excel (2007-)", "xlsx", true, true, false),
    XLTM(OfficeApplication.EXCEL, "Workbook template for Excel (2007-)", "xlsm", true, true, true),

    XLA(OfficeApplication.EXCEL, "Add-In for Excel (97-2003)", "xla", false, false, true),
    XLAM(OfficeApplication.EXCEL, "Add-In for Excel (2007-)", "xlam", true, true, true),

    ODS(OfficeApplication.EXCEL, "OpenDocument Spreadsheet", "ods", true, true, false),
    OTS(OfficeApplication.EXCEL, "OpenDocument Template", "ots", true, true, false),

    /**
     * PowerPoint formats
     */
    PPT(OfficeApplication.POWERPOINT, "Presentation for PowerPoint (97-2003)", "ppt", false, false, true),
    PPTX(OfficeApplication.POWERPOINT, "Presentation for PowerPoint (2007-)", "pptx", true, true, false),
    PPTM(OfficeApplication.POWERPOINT, "Presentation for PowerPoint (2007-)", "pptm", true, true, true),

    POT(OfficeApplication.POWERPOINT, "Presentation template for PowerPoint (97-2003)", "pot", false, false, true),
    POTX(OfficeApplication.POWERPOINT, "Presentation template for PowerPoint (2007-)", "potx", true, true, false),
    POTM(OfficeApplication.POWERPOINT, "Presentation template for PowerPoint (2007-)", "potm", true, true, true),

    PPS(OfficeApplication.POWERPOINT, "Presentation Slide Show for PowerPoint (97-2003)", "pps", false, true, true),
    PPSX(OfficeApplication.POWERPOINT, "Presentation Slide Show for PowerPoint (2007-)", "ppsx", true, true, false),
    PPSM(OfficeApplication.POWERPOINT, "Presentation Slide Show for PowerPoint (2007-)", "ppsm", true, true, true),

    PPA(OfficeApplication.POWERPOINT, "Add-In for PowerPoint (97-2003)", "ppa", false, true, true),
    PPSA(OfficeApplication.POWERPOINT, "Add-In for PowerPoint (2007-)", "ppsa", true, true, true),

    ODP(OfficeApplication.POWERPOINT, "OpenDocument Presentation", "odp", true, true, false),
    OTP(OfficeApplication.POWERPOINT, "OpenDocument Presentation Template", "otp", true, true, false),

    /**
     * Visio formats TODO: VDX, VSX, VTX, if supported by poi
     */
    VSD(OfficeApplication.VISIO, "Drawing for Visio (-2010)", "vsd", false, false, true),
    VSDX(OfficeApplication.VISIO, "Drawing for Visio (2013-)", "vsdx", true, true, false),
    VSDM(OfficeApplication.VISIO, "Drawing for Visio (2013-)", "vsdm", true, true, true),

    VSS(OfficeApplication.VISIO, "Stencil for Visio (-2010)", "vsd", false, false, true),
    VSSX(OfficeApplication.VISIO, "Stencil for Visio (2013-)", "vssx", true, true, false),
    VSSM(OfficeApplication.VISIO, "Stencil for Visio (2013-)", "vssm", true, true, true),

    VST(OfficeApplication.VISIO, "Template for Visio (-2010)", "vsd", false, false, true),
    VSTX(OfficeApplication.VISIO, "Template for Visio (2013-)", "vstx", true, true, false),
    VSTM(OfficeApplication.VISIO, "Template for Visio (2013-)", "vstm", true, true, true),

    /**
     * Publisher formats
     */
    PUB(OfficeApplication.PUBLISHER, "", "pub", true, true, false),

    /**
     * Outlook formats
     */
    PST(OfficeApplication.OUTLOOK, "Personal Folders", "pst", false, false, false),

    /**
     * Access formats
     */
    MDA(OfficeApplication.ACCESS, "Add-In for Access (97-2003)", "mda", false, false, true),
    MDB(OfficeApplication.ACCESS, "Database for Access (97-2003)", "mdb", false, false, true),
    MDE(OfficeApplication.ACCESS, "Database for Access (97-2003)", "mde", false, false, true),
    ACCDB(OfficeApplication.ACCESS, "Database for Access (2007-)", "accdb", false, false, true),

    /**
     * Project formats
     */
    MPP(OfficeApplication.PROJECT, "Project Plan for Project", "mpp", false, false, false),
    MPT(OfficeApplication.PROJECT, "Project Plan Template for Project", "mpt", false, false, false);

    @Getter
    private final OfficeApplication officeApplication;
    @Getter
    private final String name;
    @Getter
    private final String extension;
    @Getter
    private final boolean zipped;
    @Getter
    private final boolean xmlBased;
    @Getter
    private final boolean canExecuteMacros;
    @Getter
    private final String description;

    OfficeFileFormat(OfficeApplication officeApplication, String name, String extension, boolean zipped,
        boolean xmlBased, boolean canExecuteMacros) {
        this.officeApplication = officeApplication;
        this.name = name;
        this.extension = extension;
        this.zipped = zipped;
        this.xmlBased = xmlBased;
        this.canExecuteMacros = canExecuteMacros;

        StringBuilder sb = new StringBuilder();
        sb.append(name);
        if (canExecuteMacros) {
            sb.append(" with macro execution capability");
        }

        if (extension != null && !extension.isEmpty()) {
            sb.append(" (*.");
            sb.append(extension);
            sb.append(")");
        }

        if (xmlBased) {
            sb.append(" content based on XML");
        }

        if (zipped) {
            sb.append(" embedded in zip container");
        }

        this.description = sb.toString();

    }

    private static String getFilenameExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        // if the last dot is after the last backslash, we have a file extension
        if (lastDot > 0 && filename.lastIndexOf('\\') < lastDot) {
            return filename.substring(lastDot + 1);
        } else {
            return "";
        }
    }

    public static OfficeFileFormat fromFilename(String filename) {
        String extension = getFilenameExtension(filename).toLowerCase();
        for (OfficeFileFormat format : values()) {
            if (format.extension.equals(extension)) {
                return format;
            }
        }
        return UNKNOWN;
    }

    public boolean isKnown() {
        return this != UNKNOWN;
    }

    public String toString() {
        return name + (canExecuteMacros ? " with macro execution capability" : "") + " (*." + extension + ")";
    }

}
