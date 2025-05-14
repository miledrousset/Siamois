package fr.siamois.domain.services.auth;

import fr.siamois.domain.models.UserInfoWithTeams;
import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.institution.Team;
import fr.siamois.domain.models.permission.EntityPermission;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.infrastructure.database.repositories.PermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.SortedSet;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean hasPermission(UserInfoWithTeams user, ActionUnit actionUnit, EntityPermission.PermissionType permissionType) {
        SortedSet<Team> userTeams = user.getTeams();
        List<EntityPermission> permission = permissionRepository.findPermissionsOfActionUnitById(actionUnit.getId());
        return permission.stream()
                .anyMatch(p -> userTeams.contains(p.getTeam()) && p.hasPermission(permissionType));
    }

    public boolean hasPermission(UserInfoWithTeams user, SpatialUnit spatialUnit, EntityPermission.PermissionType permissionType) {
        SortedSet<Team> userTeams = user.getTeams();
        List<EntityPermission> permission = permissionRepository.findPermissionsOfSpatialUnitById(spatialUnit.getId());
        return permission.stream()
                .anyMatch(p -> userTeams.contains(p.getTeam()) && p.hasPermission(permissionType));
    }

    public boolean hasPermission(UserInfoWithTeams user, RecordingUnit recordingUnit, EntityPermission.PermissionType permissionType) {
        SortedSet<Team> userTeams = user.getTeams();
        List<EntityPermission> permission = permissionRepository.findPermissionsOfRecordingUnitById(recordingUnit.getId());
        return permission.stream()
                .anyMatch(p -> userTeams.contains(p.getTeam()) && p.hasPermission(permissionType));
    }

}
