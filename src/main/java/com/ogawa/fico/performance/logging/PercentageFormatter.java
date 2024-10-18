package com.ogawa.fico.performance.logging;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class PercentageFormatter {

    final DecimalFormat fractionalNumberFormatter;

    PercentageFormatter() {
        this(Locale.getDefault(Locale.Category.FORMAT));
    }

    PercentageFormatter(Locale locale) {
        this(locale, 1);
    }

    PercentageFormatter(Locale locale, int fractionDigits) {

        if (fractionDigits < 0) {
            throw new IllegalArgumentException("Fraction digits must be non-negative");
        }

        String fractionalNumberPattern;

        if (fractionDigits == 0) {
            fractionalNumberPattern = "#,##0'%'";
        } else {
            fractionalNumberPattern = "#,##0." + "0".repeat(fractionDigits) + "'%'";
        }

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);

        this.fractionalNumberFormatter = new DecimalFormat(fractionalNumberPattern, symbols);

    }

    public String format(Double dbl) {
        return fractionalNumberFormatter.format(dbl);
    }

}
