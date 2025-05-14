package fr.siamois.domain.services.auth;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.institution.Team;
import fr.siamois.domain.models.permission.EntityPermission;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.services.person.TeamService;
import fr.siamois.infrastructure.database.repositories.PermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class PermissionService {

    private final TeamService teamService;
    private final PermissionRepository permissionRepository;

    public PermissionService(TeamService teamService, PermissionRepository permissionRepository) {
        this.teamService = teamService;
        this.permissionRepository = permissionRepository;
    }

    public boolean hasPermission(UserInfo user, ActionUnit actionUnit, EntityPermission.PermissionType permissionType) {
        Set<Team> userTeams = teamService.findTeamsOfPersonInInstitution(user);
        List<EntityPermission> permission = permissionRepository.findPermissionsOfActionUnitById(actionUnit.getId());
        return permission.stream()
                .anyMatch(p -> userTeams.contains(p.getTeam()) && p.hasPermission(permissionType));
    }

    public boolean hasPermission(UserInfo user, SpatialUnit spatialUnit, EntityPermission.PermissionType permissionType) {
        Set<Team> userTeams = teamService.findTeamsOfPersonInInstitution(user);
        List<EntityPermission> permission = permissionRepository.findPermissionsOfSpatialUnitById(spatialUnit.getId());
        return permission.stream()
                .anyMatch(p -> userTeams.contains(p.getTeam()) && p.hasPermission(permissionType));
    }

    public boolean hasPermission(UserInfo user, RecordingUnit recordingUnit, EntityPermission.PermissionType permissionType) {
        Set<Team> userTeams = teamService.findTeamsOfPersonInInstitution(user);
        List<EntityPermission> permission = permissionRepository.findPermissionsOfRecordingUnit(recordingUnit.getId());
        return permission.stream()
                .anyMatch(p -> userTeams.contains(p.getTeam()) && p.hasPermission(permissionType));
    }

}
