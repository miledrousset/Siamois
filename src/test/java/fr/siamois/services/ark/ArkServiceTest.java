package fr.siamois.services.ark;

import fr.siamois.infrastructure.repositories.ArkRepository;
import fr.siamois.models.Institution;
import fr.siamois.models.ark.Ark;
import fr.siamois.models.exceptions.ark.NoArkConfigException;
import fr.siamois.models.exceptions.ark.TooManyGenerationsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArkServiceTest {

    @Mock private ArkRepository arkRepository;
    @Mock private NoidCheckService noidCheckService;

    private ArkService arkService;

    @BeforeEach
    void beforeEach() {
        arkService = new ArkService(noidCheckService, arkRepository);
    }

    @Test
    void arkNotExistInInstitution_shouldReturnFalse_whenQualifierExist() {
        when(arkRepository.existsByInstitutionAndQualifier(anyLong(), anyString())).thenReturn(true);

        Institution institution = new Institution();
        institution.setId(1L);

        boolean result = arkService.qualifierNotExistInInstitution(institution, "KHKFJNS-B");

        assertFalse(result);
    }

    @Test
    void generateAndSave_shouldGenerateAndSaveArk() throws NoArkConfigException, TooManyGenerationsException {
        Institution institution = new Institution();
        institution.setId(1L);
        institution.setArkNaan("12345");
        institution.setArkSize(5);

        when(noidCheckService.calculateCheckDigit(anyString())).thenReturn("X");
        when(arkRepository.existsByInstitutionAndQualifier(anyLong(), anyString())).thenReturn(false);
        when(arkRepository.save(any(Ark.class))).thenAnswer(invocation -> {
            Ark ark = invocation.getArgument(0, Ark.class);
            ark.setInternalId(1L);
            return ark;
        });

        Ark ark = arkService.generateAndSave(institution);

        assertNotNull(ark);
        assertEquals(institution, ark.getCreatingInstitution());
        assertTrue(ark.getQualifier().matches("[a-z0-9]{5}-X"));
    }

    @Test
    void generateAndSave_shouldThrowNoArkConfigException_whenArkNaanIsNull() {
        Institution institution = new Institution();
        institution.setId(1L);
        institution.setArkNaan(null); // No ARK NAAN set

        assertThrows(NoArkConfigException.class, () -> arkService.generateAndSave(institution));
    }

    @Test
    void generateAndSave_shouldThrowTooManyGenerationsException_whenMaxGenerationsReached() {
        Institution institution = new Institution();
        institution.setId(1L);
        institution.setArkNaan("12345");
        institution.setArkSize(3);

        when(noidCheckService.calculateCheckDigit(anyString())).thenReturn("X");
        when(arkRepository.existsByInstitutionAndQualifier(anyLong(), anyString())).thenReturn(true);

        assertThrows(TooManyGenerationsException.class, () -> {
            arkService.generateAndSave(institution);
        });
    }
}