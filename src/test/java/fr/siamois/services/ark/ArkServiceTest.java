package fr.siamois.services.ark;

import fr.siamois.infrastructure.repositories.ArkRepository;
import fr.siamois.models.Institution;
import fr.siamois.models.ark.Ark;
import fr.siamois.models.exceptions.ark.NoArkConfigException;
import fr.siamois.models.exceptions.ark.TooManyGenerationsException;
import fr.siamois.models.settings.InstitutionSettings;
import fr.siamois.services.InstitutionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArkServiceTest {

    @Mock private ArkRepository arkRepository;
    @Mock private NoidCheckService noidCheckService;
    @Mock private InstitutionService institutionService;

    private ArkService arkService;

    @BeforeEach
    void beforeEach() {
        arkService = new ArkService(noidCheckService, arkRepository, institutionService);
    }

    @Test
    void arkNotExistInInstitution_shouldReturnFalse_whenQualifierExist() {
        when(arkRepository.findByInstitutionAndQualifier(anyLong(), anyString())).thenReturn(Optional.of(new Ark()));

        Institution institution = new Institution();
        institution.setId(1L);

        boolean result = arkService.qualifierNotExistInInstitution(institution, "KHKFJNS-B");

        assertFalse(result);
    }

    @Test
    void generateAndSave_shouldGenerateAndSaveArk() throws NoArkConfigException, TooManyGenerationsException {
        Institution institution = new Institution();
        institution.setId(1L);

        InstitutionSettings settings = new InstitutionSettings();
        settings.setInstitution(institution);
        settings.setArkNaan("12345");
        settings.setArkSize(5);

        when(noidCheckService.calculateCheckDigit(anyString())).thenReturn("X");
        when(arkRepository.findByInstitutionAndQualifier(anyLong(), anyString())).thenReturn(Optional.empty());
        when(arkRepository.save(any(Ark.class))).thenAnswer(invocation -> {
            Ark ark = invocation.getArgument(0, Ark.class);
            ark.setInternalId(1L);
            return ark;
        });

        Ark ark = arkService.generateAndSave(settings);

        assertNotNull(ark);
        assertEquals(institution, ark.getCreatingInstitution());
        assertTrue(ark.getQualifier().matches("[a-z0-9]{5}-X"));
    }

    @Test
    void generateAndSave_shouldThrowNoArkConfigException_whenArkNaanIsNull() {
        Institution institution = new Institution();
        institution.setId(1L);

        InstitutionSettings empty = new InstitutionSettings();
        empty.setInstitution(institution);

        assertThrows(NoArkConfigException.class, () -> arkService.generateAndSave(empty));
    }

    @Test
    void generateAndSave_shouldThrowTooManyGenerationsException_whenMaxGenerationsReached() {
        Institution institution = new Institution();
        institution.setId(1L);

        InstitutionSettings settings = new InstitutionSettings();
        settings.setInstitution(institution);
        settings.setArkNaan("12345");
        settings.setArkSize(3);

        when(noidCheckService.calculateCheckDigit(anyString())).thenReturn("X");
        when(arkRepository.findByInstitutionAndQualifier(anyLong(), anyString())).thenReturn(Optional.of(new Ark()));

        assertThrows(TooManyGenerationsException.class, () -> arkService.generateAndSave(settings));
    }
}