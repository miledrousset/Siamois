package fr.siamois.models.history;

import fr.siamois.models.spatialunit.SpatialUnit;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HistoryEntryTest {

    @Test
    void createOriginal() {

        SpatialUnitHist hist = new SpatialUnitHist();
        hist.setId(1L);
        hist.setTableId(12L);
        hist.setCreationTime(OffsetDateTime.now());
        hist.setName("Ionia");

        SpatialUnit original = hist.createOriginal(SpatialUnit.class);

        assertEquals(12L, original.getId());
        assertEquals("Ionia", original.getName());
        assertEquals(hist.getCreationTime(), original.getCreationTime());
    }
}