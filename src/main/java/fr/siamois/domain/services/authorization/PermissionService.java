package fr.siamois.domain.services.authorization;

import fr.siamois.domain.models.UserInfo;
import org.springframework.stereotype.Service;

@Service
public interface PermissionService {
    boolean hasReadPermission(UserInfo user, Object resource);
    boolean hasWritePermission(UserInfo user, Object resource);
    boolean isInstitutionManager(UserInfo user);
    boolean isActionManager(UserInfo user);
}
