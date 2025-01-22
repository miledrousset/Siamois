package fr.siamois.models.history;

import fr.siamois.models.Team;
import fr.siamois.utils.HistoryCopyUtils;

import java.time.OffsetDateTime;

public interface HistoryEntry<T> {
    HistoryUpdateType getUpdateType();
    Long getTableId();
    OffsetDateTime getUpdateTime();
    default T createOriginal(Class<T> originalClass) {
        try {
            T original = originalClass.getDeclaredConstructor().newInstance();
            HistoryCopyUtils.copyAttributesFromHistToTarget(this, original);
            return original;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create original instance", e);
        }
    }
    Team getAuthorTeam();
}

