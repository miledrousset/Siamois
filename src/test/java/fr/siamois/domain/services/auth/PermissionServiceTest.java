package fr.siamois.domain.services.auth;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.UserInfoWithTeams;
import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.institution.Team;
import fr.siamois.domain.models.permission.EntityPermission;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.infrastructure.database.repositories.PermissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private PermissionService permissionService;

    private UserInfoWithTeams user;
    private Team team;
    private EntityPermission permission;

    @BeforeEach
    void setUp() {
        team = new Team();
        team.setId(1L);

        SortedSet<Team> userTeams = new TreeSet<>();
        userTeams.add(team);

        Institution institution = new Institution();
        institution.setId(1L);
        institution.setName("Test Institution");

        Person person = new Person();
        person.setId(1L);
        person.setName("Test Person");
        person.setEmail("mail@42.com");

        user = new UserInfoWithTeams(new UserInfo(institution, person, "fr"), userTeams);

        permission = mock(EntityPermission.class);
    }

    @Test
    void testHasPermission_ActionUnit_WithPermission() {
        ActionUnit actionUnit = new ActionUnit();
        actionUnit.setId(1L);

        when(permissionRepository.findPermissionsOfActionUnitById(actionUnit.getId()))
                .thenReturn(List.of(permission));

        when(permission.getTeam()).thenReturn(team);
        when(permission.hasPermission(any(EntityPermission.PermissionType.class))).thenReturn(true);

        boolean result = permissionService.hasPermission(user, actionUnit, EntityPermission.PermissionType.READ);

        assertTrue(result);
        verify(permissionRepository).findPermissionsOfActionUnitById(actionUnit.getId());
    }

    @Test
    void testHasPermission_ActionUnit_WithoutPermission() {
        ActionUnit actionUnit = new ActionUnit();
        actionUnit.setId(1L);

        when(permissionRepository.findPermissionsOfActionUnitById(actionUnit.getId()))
                .thenReturn(List.of());

        boolean result = permissionService.hasPermission(user, actionUnit, EntityPermission.PermissionType.READ);

        assertFalse(result);
        verify(permissionRepository).findPermissionsOfActionUnitById(actionUnit.getId());
    }

    @Test
    void testHasPermission_SpatialUnit_WithPermission() {
        SpatialUnit spatialUnit = new SpatialUnit();
        spatialUnit.setId(1L);

        when(permissionRepository.findPermissionsOfSpatialUnitById(spatialUnit.getId()))
                .thenReturn(List.of(permission));

        when(permission.getTeam()).thenReturn(team);
        when(permission.hasPermission(any(EntityPermission.PermissionType.class))).thenReturn(true);

        boolean result = permissionService.hasPermission(user, spatialUnit, EntityPermission.PermissionType.UPDATE);

        assertTrue(result);
        verify(permissionRepository).findPermissionsOfSpatialUnitById(spatialUnit.getId());
    }

    @Test
    void testHasPermission_SpatialUnit_WithoutPermission() {
        SpatialUnit spatialUnit = new SpatialUnit();
        spatialUnit.setId(1L);

        when(permissionRepository.findPermissionsOfSpatialUnitById(spatialUnit.getId()))
                .thenReturn(List.of());

        boolean result = permissionService.hasPermission(user, spatialUnit, EntityPermission.PermissionType.UPDATE);

        assertFalse(result);
        verify(permissionRepository).findPermissionsOfSpatialUnitById(spatialUnit.getId());
    }

    @Test
    void testHasPermission_RecordingUnit_WithPermission() {
        RecordingUnit recordingUnit = new RecordingUnit();
        recordingUnit.setId(1L);

        when(permissionRepository.findPermissionsOfRecordingUnitById(recordingUnit.getId()))
                .thenReturn(List.of(permission));
        when(permission.getTeam()).thenReturn(team);
        when(permission.hasPermission(EntityPermission.PermissionType.READ)).thenReturn(Boolean.TRUE);

        boolean result = permissionService.hasPermission(user, recordingUnit, EntityPermission.PermissionType.READ);

        assertTrue(result);
        verify(permissionRepository).findPermissionsOfRecordingUnitById(recordingUnit.getId());
    }

    @Test
    void testHasPermission_RecordingUnit_WithoutPermission() {
        RecordingUnit recordingUnit = new RecordingUnit();
        recordingUnit.setId(1L);

        when(permissionRepository.findPermissionsOfRecordingUnitById(recordingUnit.getId()))
                .thenReturn(List.of());

        boolean result = permissionService.hasPermission(user, recordingUnit, EntityPermission.PermissionType.READ);

        assertFalse(result);
        verify(permissionRepository).findPermissionsOfRecordingUnitById(recordingUnit.getId());
    }
}