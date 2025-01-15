package fr.siamois.models.history;

import java.time.OffsetDateTime;

public interface HistoryEntry {
    HistoryUpdateType getUpdateType();
    Long getTableId();
    OffsetDateTime getUpdateTime();
}
