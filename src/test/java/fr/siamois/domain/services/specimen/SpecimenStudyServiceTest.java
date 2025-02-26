package fr.siamois.domain.services.specimen;

import fr.siamois.domain.services.specimen.SpecimenStudyService;
import fr.siamois.infrastructure.repositories.specimen.SpecimenStudyRepository;
import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.specimen.SpecimenStudy;
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
class SpecimenStudyServiceTest {

    @Mock
    private SpecimenStudyRepository specimenStudyRepository;

    private SpecimenStudyService specimenStudyService;

    @BeforeEach
    void setUp() {
        specimenStudyService = new SpecimenStudyService(specimenStudyRepository);
    }

    @Test
    void findWithoutArk() {
        Institution institution = new Institution();
        institution.setId(1L);
        SpecimenStudy specimenStudy = new SpecimenStudy();

        when(specimenStudyRepository.findAllByArkIsNullAndCreatedByInstitution(institution))
                .thenReturn(Collections.singletonList(specimenStudy));

        List<SpecimenStudy> result = specimenStudyService.findWithoutArk(institution);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(specimenStudyRepository, times(1))
                .findAllByArkIsNullAndCreatedByInstitution(institution);
    }

    @Test
    void save() {
        SpecimenStudy specimenStudy = new SpecimenStudy();

        when(specimenStudyRepository.save(specimenStudy)).thenReturn(specimenStudy);

        SpecimenStudy result = (SpecimenStudy) specimenStudyService.save(specimenStudy);

        assertNotNull(result);
        assertEquals(specimenStudy, result);
        verify(specimenStudyRepository, times(1)).save(specimenStudy);
    }
}