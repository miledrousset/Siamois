package fr.siamois.domain.services.authorization.writeverifier;

import fr.siamois.domain.models.TraceableEntity;
import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecordingUnitWriteVerifierTest {

    @Mock
    private ActionUnitWriteVerifier actionUnitWriteVerifier;

    @InjectMocks
    private RecordingUnitWriteVerifier recordingUnitWriteVerifier;

    private UserInfo userInfo;

    @BeforeEach
    void setUp() {
        Person person = new Person();
        person.setUsername("testUser");
        person.setId(1L);

        Institution institution = new Institution();
        institution.setId(1L);
        institution.setName("Test Institution");

        userInfo = new UserInfo(institution, person, "fr");
    }

    @Test
    void getEntityClass_shouldReturnRecordingUnitClass() {
        // Act
        Class<? extends TraceableEntity> result = recordingUnitWriteVerifier.getEntityClass();

        // Assert
        assertEquals(RecordingUnit.class, result);
    }

    @Test
    void hasSpecificWritePermission_shouldReturnTrueWhenPermissionExists() {
        // Arrange
        RecordingUnit recordingUnit = new RecordingUnit();
        recordingUnit.setId(1L);

        when(actionUnitWriteVerifier.hasSpecificWritePermission(userInfo, recordingUnit)).thenReturn(true);

        // Act
        boolean result = recordingUnitWriteVerifier.hasSpecificWritePermission(userInfo, recordingUnit);

        // Assert
        assertTrue(result);
    }

    @Test
    void hasSpecificWritePermission_shouldReturnFalseWhenPermissionDoesNotExist() {
        // Arrange
        RecordingUnit recordingUnit = new RecordingUnit();
        recordingUnit.setId(1L);

        when(actionUnitWriteVerifier.hasSpecificWritePermission(userInfo, recordingUnit)).thenReturn(false);

        // Act
        boolean result = recordingUnitWriteVerifier.hasSpecificWritePermission(userInfo, recordingUnit);

        // Assert
        assertFalse(result);
    }

}