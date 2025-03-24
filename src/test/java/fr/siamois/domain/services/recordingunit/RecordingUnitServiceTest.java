package fr.siamois.domain.services.recordingunit;

import fr.siamois.domain.models.ArkEntity;
import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.services.form.CustomFormResponseService;
import fr.siamois.domain.services.vocabulary.ConceptService;
import fr.siamois.infrastructure.repositories.recordingunit.RecordingUnitRepository;
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
    private StratigraphicRelationshipService stratigraphicRelationshipService;

    private RecordingUnitService recordingUnitService;

    @BeforeEach
    void beforeEach() {
        recordingUnitService = new RecordingUnitService(recordingUnitRepository, conceptService,
                customFormResponseService);
    }

    @Test
    void findAllByActionUnit() {
        ActionUnit actionUnit = new ActionUnit();
        actionUnit.setId(1L);
        RecordingUnit recordingUnit = new RecordingUnit();
        recordingUnit.setActionUnit(actionUnit);

        when(recordingUnitRepository.findAllByActionUnit(actionUnit)).thenReturn(Collections.singletonList(recordingUnit));

        List<RecordingUnit> result = recordingUnitService.findAllByActionUnit(actionUnit);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(actionUnit, result.get(0).getActionUnit());
        verify(recordingUnitRepository, times(1)).findAllByActionUnit(actionUnit);
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
}