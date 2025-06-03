package fr.siamois.domain.services.authorization;

import fr.siamois.domain.models.TraceableEntity;
import fr.siamois.domain.models.UserInfo;
import org.springframework.stereotype.Service;

@Service
public interface PermissionService {
    boolean hasReadPermission(UserInfo user, TraceableEntity resource);
    boolean hasWritePermission(UserInfo user, TraceableEntity resource);
    boolean isInstitutionManager(UserInfo user);
    boolean isActionManager(UserInfo user);
}
