package fr.siamois.domain.models.history;

import fr.siamois.domain.utils.HistoryCopyUtils;

import java.io.Serializable;
import java.time.OffsetDateTime;

public interface HistoryEntry<T> extends Serializable {
    Long getTableId();
    OffsetDateTime getUpdateTime();
    default T createOriginal(Class<T> originalClass) {
        try {
            T original = originalClass.getDeclaredConstructor().newInstance();
            HistoryCopyUtils.copyAttributesFromHistToTarget(this, original);
            return original;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create original instance", e);
        }
    }
}

