package fr.siamois.domain.services.authorization.writeverifier;

import fr.siamois.domain.models.TraceableEntity;
import fr.siamois.domain.models.UserInfo;

public interface WritePermissionVerifier {
    Class<? extends TraceableEntity> getEntityClass();
    boolean hasSpecificWritePermission(UserInfo userInfo, TraceableEntity resource);
}
