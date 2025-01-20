package fr.siamois.utils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * offsetDateTime to {@link String} representation with format `YYYY-MM-DD HH:MM` with the system time zone.
     * @param offsetDateTime The {@link OffsetDateTime} to convert
     * @return The string representation of the offsetDateTime at the system time zone
     */
    public static String formatOffsetDateTime(OffsetDateTime offsetDateTime) {
        LocalDateTime dateTime = offsetDateTime.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        return formatter.format(dateTime);
    }

}
