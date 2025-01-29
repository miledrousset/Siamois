package fr.siamois.services.vocabulary;

import fr.siamois.infrastructure.api.ConceptApi;
import fr.siamois.infrastructure.repositories.SpatialUnitRepository;
import fr.siamois.infrastructure.repositories.ark.ArkRepository;
import fr.siamois.infrastructure.repositories.ark.ArkServerRepository;
import fr.siamois.infrastructure.repositories.vocabulary.ConceptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FieldServiceTest {

    @Mock private ConceptApi conceptApi;
    @Mock private ConceptRepository conceptRepository;
    @Mock private ArkServerRepository arkServerRepository;
    @Mock private ArkRepository arkRepository;
    @Mock private SpatialUnitRepository spatialUnitRepository;

    private FieldService fieldService;

    @BeforeEach
    public void beforeEach() {
        fieldService = new FieldService(conceptApi,
                conceptRepository,
                arkServerRepository,
                arkRepository,
                spatialUnitRepository);
    }

    @Test
    public void testSearchAllFieldCodes_hasAtLeaseThreeFields() {
        List<String> result = fieldService.searchAllFieldCodes();

        assertThat(result.size()).isGreaterThanOrEqualTo(3);
    }

}