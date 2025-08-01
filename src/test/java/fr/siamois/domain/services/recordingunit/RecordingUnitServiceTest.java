package fr.siamois.domain.services.recordingunit;

import fr.siamois.domain.models.ArkEntity;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.services.form.CustomFormResponseService;
import fr.siamois.domain.services.vocabulary.ConceptService;
import fr.siamois.infrastructure.database.repositories.person.PersonRepository;
import fr.siamois.infrastructure.database.repositories.recordingunit.RecordingUnitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecordingUnitServiceTest {

    @Mock
    private RecordingUnitRepository recordingUnitRepository;

    @Mock
    private ConceptService conceptService;
    @Mock
    private CustomFormResponseService customFormResponseService;
    @Mock
    private PersonRepository personRepository;

    @Mock
    private StratigraphicRelationshipService stratigraphicRelationshipService;

    private RecordingUnitService recordingUnitService;

    @BeforeEach
    void beforeEach() {
        recordingUnitService = new RecordingUnitService(recordingUnitRepository, conceptService,
                customFormResponseService,personRepository);
    }

    @Test
    void findWithoutArk() {
        Institution institution = new Institution();
        institution.setId(1L);
        RecordingUnit recordingUnit = new RecordingUnit();

        when(recordingUnitRepository.findAllWithoutArkOfInstitution(institution.getId())).thenReturn(Collections.singletonList(recordingUnit));

        List<? extends ArkEntity> result = recordingUnitService.findWithoutArk(institution);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(recordingUnitRepository, times(1)).findAllWithoutArkOfInstitution(institution.getId());
    }

    @Test
    void save() {
        RecordingUnit recordingUnit = new RecordingUnit();

        when(recordingUnitRepository.save(any(RecordingUnit.class))).thenReturn(recordingUnit);

        ArkEntity result = recordingUnitService.save(recordingUnit);

        assertNotNull(result);
        assertEquals(recordingUnit, result);
    }

    @Test
    void countByInstitution_success() {
        when(recordingUnitRepository.countByCreatedByInstitution(any(Institution.class))).thenReturn(3L);
        assertEquals(3,recordingUnitService.countByInstitution(new Institution()));
    }
}