package edu.kalinin.acc.helper;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Pattern;

@UtilityClass
public class ParseHelper {
    public static BigDecimal toBigDecimal(String value) {
        return new BigDecimal(value);
    }

    public static Date toDate(String str) {
        var formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return new Date(formatter.parse(str).getTime());
        } catch (ParseException e) {
            return null;
        }
    }

    public static OffsetDateTime toOffsetDateTime(String str) {
        return OffsetDateTime.parse(str);
    }

    public static LocalDate toLocalDate(String str) {
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(str, formatter);
    }

    public static Optional<String> findInJson(String field, String str) {

        var matcher = Pattern.compile("\"" + field + "\"\\s*:\\s*(.*?),").matcher(str);
        if (matcher.find()) {
            var value = matcher.group(1);
            return Optional.of(value);
        } else return Optional.empty();
    }
}
