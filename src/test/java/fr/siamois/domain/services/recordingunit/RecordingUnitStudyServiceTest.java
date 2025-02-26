package fr.siamois.domain.services.recordingunit;

import fr.siamois.domain.services.recordingunit.RecordingUnitStudyService;
import fr.siamois.infrastructure.repositories.recordingunit.RecordingUnitStudyRepository;
import fr.siamois.domain.models.ArkEntity;
import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.recordingunit.RecordingUnitStudy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecordingUnitStudyServiceTest {

    @Mock
    private RecordingUnitStudyRepository recordingUnitStudyRepository;

    private RecordingUnitStudyService recordingUnitStudyService;

    @BeforeEach
    void setUp() {
        recordingUnitStudyService = new RecordingUnitStudyService(recordingUnitStudyRepository);
    }

    @Test
    void findWithoutArk() {
        Institution institution = new Institution();
        institution.setId(1L);
        RecordingUnitStudy recordingUnitStudy = new RecordingUnitStudy();

        when(recordingUnitStudyRepository.findAllByArkIsNullAndCreatedByInstitution(institution))
                .thenReturn(Collections.singletonList(recordingUnitStudy));

        List<? extends ArkEntity> result = recordingUnitStudyService.findWithoutArk(institution);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(recordingUnitStudyRepository, times(1))
                .findAllByArkIsNullAndCreatedByInstitution(institution);
    }

    @Test
    void save() {
        RecordingUnitStudy recordingUnitStudy = new RecordingUnitStudy();

        when(recordingUnitStudyRepository.save(recordingUnitStudy)).thenReturn(recordingUnitStudy);

        ArkEntity result = recordingUnitStudyService.save(recordingUnitStudy);

        assertNotNull(result);
        assertEquals(recordingUnitStudy, result);
        verify(recordingUnitStudyRepository, times(1)).save(recordingUnitStudy);
    }
}