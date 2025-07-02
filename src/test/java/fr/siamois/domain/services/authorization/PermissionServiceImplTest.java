package fr.siamois.domain.services.authorization;

import fr.siamois.domain.models.TraceableEntity;
import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.services.InstitutionService;
import fr.siamois.domain.services.authorization.writeverifier.WritePermissionVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionServiceImplTest {

    @Mock
    private InstitutionService institutionService;

    @Mock
    private WritePermissionVerifier writePermissionVerifier;

    @Mock
    private TraceableEntity resource;

    @Mock
    private UserInfo user;

    private PermissionServiceImpl permissionService;

    private Person person;
    private Institution institutionA, institutionB;

    @BeforeEach
    void setUp() {
        permissionService = new PermissionServiceImpl(institutionService, List.of(writePermissionVerifier));

        person = new Person();
        person.setUsername("username");
        person.setId(1L);

        institutionA = new Institution();
        institutionA.setName("Institution A");
        institutionA.setId(1L);

        institutionB = new Institution();
        institutionB.setName("Institution B");
        institutionB.setId(2L);
    }

    @Test
    void hasReadPermission_shouldReturnFalseWhenInstitutionDoesNotMatch() {
        when(resource.getCreatedByInstitution()).thenReturn(institutionA);
        when(user.getInstitution()).thenReturn(institutionB);

        boolean result = permissionService.hasReadPermission(user, resource);

        assertFalse(result);
    }

    @Test
    void hasReadPermission_shouldReturnTrueWhenUserIsActionManager() {
        when(resource.getCreatedByInstitution()).thenReturn(institutionA);
        when(user.getInstitution()).thenReturn(institutionA);
        when(permissionService.isActionManager(user)).thenReturn(true);

        boolean result = permissionService.hasReadPermission(user, resource);

        assertTrue(result);
    }

    @Test
    void hasReadPermission_shouldReturnTrueWhenUserIsInstitutionManager() {
        when(resource.getCreatedByInstitution()).thenReturn(institutionA);
        when(user.getInstitution()).thenReturn(institutionA);
        when(permissionService.isInstitutionManager(user)).thenReturn(true);

        boolean result = permissionService.hasReadPermission(user, resource);

        assertTrue(result);
    }

    @Test
    void hasReadPermission_shouldReturnTrueWhenUserIsInInstitution() {
        when(resource.getCreatedByInstitution()).thenReturn(institutionA);
        when(user.getInstitution()).thenReturn(institutionA);
        when(permissionService.isActionManager(user)).thenReturn(false);
        when(permissionService.isInstitutionManager(user)).thenReturn(false);
        when(institutionService.personIsInInstitution(user.getUser(), institutionA)).thenReturn(true);

        boolean result = permissionService.hasReadPermission(user, resource);

        assertTrue(result);
    }

    @Test
    void hasWritePermission_shouldReturnFalseWhenInstitutionDoesNotMatch() {
        when(resource.getCreatedByInstitution()).thenReturn(institutionA);
        when(user.getInstitution()).thenReturn(institutionB);

        boolean result = permissionService.hasWritePermission(user, resource);

        assertFalse(result);
    }

    @Test
    void hasWritePermission_shouldReturnTrueWhenUserIsActionManager() {
        when(resource.getCreatedByInstitution()).thenReturn(institutionA);
        when(user.getInstitution()).thenReturn(institutionA);
        when(permissionService.isActionManager(user)).thenReturn(true);

        boolean result = permissionService.hasWritePermission(user, resource);

        assertTrue(result);
    }

    @Test
    void hasWritePermission_shouldReturnTrueWhenUserIsInstitutionManager() {
        when(resource.getCreatedByInstitution()).thenReturn(institutionA);
        when(user.getInstitution()).thenReturn(institutionA);
        when(permissionService.isInstitutionManager(user)).thenReturn(true);

        boolean result = permissionService.hasWritePermission(user, resource);

        assertTrue(result);
    }

    @Test
    void hasWritePermission_shouldReturnFalseWhenVerifierExistsAndPermissionDenied() {
        when(resource.getCreatedByInstitution()).thenReturn(institutionA);
        when(user.getInstitution()).thenReturn(institutionA);
        when(permissionService.isActionManager(user)).thenReturn(false);
        when(permissionService.isInstitutionManager(user)).thenReturn(false);

        boolean result = permissionService.hasWritePermission(user, resource);

        assertFalse(result);
    }

    @Test
    void isInstitutionManager_shouldReturnTrueWhenUserIsManager() {
        when(user.getUser()).thenReturn(person);
        when(user.getInstitution()).thenReturn(institutionA);
        when(institutionService.personIsInstitutionManager(person, institutionA)).thenReturn(true);

        boolean result = permissionService.isInstitutionManager(user);

        assertTrue(result);
    }

    @Test
    void isInstitutionManager_shouldReturnFalseWhenUserIsNotManager() {
        when(user.getUser()).thenReturn(person);
        when(user.getInstitution()).thenReturn(institutionA);
        when(institutionService.personIsInstitutionManager(person, institutionA)).thenReturn(false);

        boolean result = permissionService.isInstitutionManager(user);

        assertFalse(result);
    }

    @Test
    void isActionManager_shouldReturnTrueWhenUserIsManager() {
        when(user.getUser()).thenReturn(person);
        when(user.getInstitution()).thenReturn(institutionA);
        when(institutionService.personIsActionManager(person, institutionA)).thenReturn(true);

        boolean result = permissionService.isActionManager(user);

        assertTrue(result);
    }

    @Test
    void isActionManager_shouldReturnFalseWhenUserIsNotManager() {
        when(user.getUser()).thenReturn(person);
        when(user.getInstitution()).thenReturn(institutionA);
        when(institutionService.personIsActionManager(person, institutionA)).thenReturn(false);

        boolean result = permissionService.isActionManager(user);

        assertFalse(result);
    }
}