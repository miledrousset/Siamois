package fr.siamois.domain.services.authorization;

import fr.siamois.domain.models.UserInfo;
import org.springframework.stereotype.Service;

@Service
public class PermissionServiceImpl implements PermissionService{

    @Override
    public boolean hasReadPermission(UserInfo user, Object resource) {
        return false;
    }

    @Override
    public boolean hasWritePermission(UserInfo user, Object resource) {
        return false;
    }

    @Override
    public boolean isInstitutionManager(UserInfo user) {
        return false;
    }

    @Override
    public boolean isActionManager(UserInfo user) {
        return false;
    }
}
