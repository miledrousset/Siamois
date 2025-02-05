package fr.siamois.utils;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilsTest {

    @Test
    void formatOffsetDateTime_shouldReturnErrorString_whenDateIsNull() {
        assertEquals("INVALID DATE", DateUtils.formatOffsetDateTime(null));
    }

    @Test
    void formatOffsetDateTime_shouldReturnDateDisplay_whenDateExist() {
        OffsetDateTime date = OffsetDateTime.now().withYear(2020)
                .withMonth(1)
                .withDayOfMonth(1)
                .withMinute(0)
                .withSecond(0)
                .withHour(0)
                .withNano(0);

        assertEquals("2020-01-01 00:00", DateUtils.formatOffsetDateTime(date));

    }

}