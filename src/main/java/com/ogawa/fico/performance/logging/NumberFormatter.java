package com.ogawa.fico.performance.logging;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class NumberFormatter {

    final DecimalFormat integralNumberFormatter;
    final DecimalFormat fractionalNumberFormatter;

    NumberFormatter() {
        this(Locale.getDefault(Locale.Category.FORMAT));
    }

    NumberFormatter(Locale locale) {
        this(locale, 2);
    }

    NumberFormatter(Locale locale, int fractionDigits) {

        if (fractionDigits < 0) {
            throw new IllegalArgumentException("Fraction digits must be non-negative");
        }

        String fractionalNumberPattern;

        if (fractionDigits == 0) {
            fractionalNumberPattern = "#,##0";
        } else {
            fractionalNumberPattern = "#,##0." + "0".repeat(fractionDigits);
        }

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);

        this.fractionalNumberFormatter = new DecimalFormat(fractionalNumberPattern, symbols);

        this.integralNumberFormatter = new DecimalFormat("#,###", symbols);

    }

    public String format(Double dbl) {
        return fractionalNumberFormatter.format(dbl);
    }

    public String format(Byte b) {
        return integralNumberFormatter.format(b);
    }

    public String format(Integer integer) {
        return integralNumberFormatter.format(integer);
    }

    public String format(Long lng) {
        return integralNumberFormatter.format(lng);
    }

}
