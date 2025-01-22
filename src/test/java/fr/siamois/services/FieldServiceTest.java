package fr.siamois.services;

import fr.siamois.infrastructure.api.ConceptApi;
import fr.siamois.infrastructure.repositories.SpatialUnitRepository;
import fr.siamois.infrastructure.repositories.ark.ArkRepository;
import fr.siamois.infrastructure.repositories.ark.ArkServerRepository;
import fr.siamois.infrastructure.repositories.vocabulary.ConceptRepository;
import fr.siamois.services.vocabulary.FieldService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class FieldServiceTest {

    @Mock
    private ConceptApi conceptApi;
    @Mock
    private ConceptRepository conceptRepository;
    @Mock
    private ArkServerRepository arkServerRepository;
    @Mock
    private ArkRepository arkRepository;
    @Mock
    private SpatialUnitRepository spatialUnitRepository;

    private final FieldService fieldService = new FieldService(conceptApi, conceptRepository, arkServerRepository, arkRepository, spatialUnitRepository);

    @Test
    void getArkIdFromUri_shouldWorkWithFullURL() {
        String uri = "https://test.server.fr/opentheso/666666/SOMEID-0";

        String arkId = fieldService.getArkIdFromUri(uri);

        assertEquals("666666/SOMEID-0", arkId);
    }

    @Test
    void getArkIdFromUri_shouldWorkWithArkURL() {
        String uri = "https://test.server.fr/opentheso/ark:/666666/SOMEID-0";

        String arkId = fieldService.getArkIdFromUri(uri);

        assertEquals("666666/SOMEID-0", arkId);
    }
}