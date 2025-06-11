package fr.siamois.domain.utils;

import fr.siamois.utils.DateUtils;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateUtilsTest {

    @Test
    void formatOffsetDateTime_shouldReturnErrorString_whenDateIsNull() {
        assertEquals("INVALID DATE", DateUtils.formatOffsetDateTime(null));
    }

    @Test
    void formatOffsetDateTime_shouldReturnDateDisplay_whenDateExist() {
        ZoneId utc = ZoneId.of("UTC");
        OffsetDateTime date = OffsetDateTime.now(utc).withYear(2020)
                .withMonth(1)
                .withDayOfMonth(1)
                .withMinute(0)
                .withSecond(0)
                .withHour(0)
                .withNano(0);

        assertEquals("2020-01-01 00:00", DateUtils.formatOffsetDateTime(date, utc));

    }

}