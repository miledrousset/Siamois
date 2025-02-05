package fr.siamois.utils;

import fr.siamois.models.spatialunit.SpatialUnit;
import fr.siamois.models.history.SpatialUnitHist;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HistoryCopyUtilsTest {

    @Test
    void copyAttributesFromHistToTarget() {
        SpatialUnitHist hist = new SpatialUnitHist();
        hist.setName("Some random name");
        hist.setTableId(12L);
        hist.setId(1L);

        SpatialUnit unit = new SpatialUnit();

        HistoryCopyUtils.copyAttributesFromHistToTarget(hist, unit);

        assertEquals(12L, unit.getId());
        assertEquals("Some random name", unit.getName());
    }
}