package com.ogawa.fico.scan.fileformat.office;

import lombok.Getter;

@SuppressWarnings("SpellCheckingInspection")
public enum OfficeApplication {

    UNKNOWN("Unknown", "", ""),
    WORD("Microsoft Word", "winword.exe", "Word.Application"),
    EXCEL("Microsoft Excel", "excel.exe", "Excel.Application"),
    POWERPOINT("Microsoft PowerPoint", "powerpnt.exe", "PowerPoint.Application"),
    ACCESS("Microsoft Access", "msaccess.exe", "Access.Application"),
    PUBLISHER("Microsoft Publisher", "mspub.exe", "Publisher.Application"),
    VISIO("Microsoft Visio", "visio.exe", "Visio.Application"),
    PROJECT("Microsoft Project", "winproj.exe", "MSProject.Application"),
    OUTLOOK("Microsoft Outlook", "outlook.exe", "Outlook.Application");

    @Getter
    private final String name;
    private final String executable;
    private final String progId;

    OfficeApplication(String name, String executable, String progId) {
        this.name = name;
        this.executable = executable;
        this.progId = progId;
    }
}
