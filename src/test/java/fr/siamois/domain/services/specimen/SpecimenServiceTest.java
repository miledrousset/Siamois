package fr.siamois.domain.services.specimen;

import fr.siamois.domain.models.ArkEntity;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.specimen.Specimen;
import fr.siamois.infrastructure.database.repositories.specimen.SpecimenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecimenServiceTest {

    @Mock
    private SpecimenRepository specimenRepository;

    private SpecimenService specimenService;

    @BeforeEach
    void setUp() {
        specimenService = new SpecimenService(specimenRepository);
    }


    @Test
    void findWithoutArk() {
        Institution institution = new Institution();
        institution.setId(1L);
        Specimen specimen = new Specimen();

        when(specimenRepository.findAllByArkIsNullAndCreatedByInstitution(institution))
                .thenReturn(List.of(specimen));

        List<? extends ArkEntity> result = specimenService.findWithoutArk(institution);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(specimenRepository, times(1))
                .findAllByArkIsNullAndCreatedByInstitution(institution);
    }

    @Test
    void save() {
        Specimen specimen = new Specimen();
        RecordingUnit ru = new RecordingUnit();
        ru.setFullIdentifier("test");
        specimen.setRecordingUnit(ru);

        when(specimenRepository.save(specimen)).thenReturn(specimen);

        ArkEntity result = specimenService.save(specimen);

        assertNotNull(result);
        assertEquals(specimen, result);
        verify(specimenRepository, times(1)).save(specimen);
    }
}