package fr.siamois.domain.utils;

import fr.siamois.domain.models.history.SpatialUnitHist;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.utils.HistoryCopyUtils;
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