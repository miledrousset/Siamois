package fr.siamois.domain.services.authorization.writeverifier;

import fr.siamois.domain.models.TraceableEntity;
import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import org.springframework.stereotype.Component;

@Component
public class RecordingUnitWriteVerifier implements WritePermissionVerifier {
    private final ActionUnitWriteVerifier actionUnitWriteVerifier;

    public RecordingUnitWriteVerifier(ActionUnitWriteVerifier actionUnitWriteVerifier) {
        this.actionUnitWriteVerifier = actionUnitWriteVerifier;
    }

    @Override
    public Class<? extends TraceableEntity> getEntityClass() {
        return RecordingUnit.class;
    }

    @Override
    public boolean hasSpecificWritePermission(UserInfo userInfo, TraceableEntity resource) {
        RecordingUnit recordingUnit = (RecordingUnit) resource;
        return actionUnitWriteVerifier.hasSpecificWritePermission(userInfo, recordingUnit);
    }
}
