package com.ogawa.fico.performance.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LocalDateTimeFormatter {

    final DateTimeFormatter formatter;

    LocalDateTimeFormatter() {
        this(Locale.getDefault(Locale.Category.FORMAT));
    }

    LocalDateTimeFormatter(Locale locale) {
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", locale);
    }

    public String format(LocalDateTime localDateTime) {
        return formatter.format(localDateTime);
    }

}
