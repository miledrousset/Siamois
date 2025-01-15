package fr.siamois.infrastructure.repositories.history;

import fr.siamois.models.history.HistoryEntry;

import java.time.OffsetDateTime;
import java.util.List;

public interface HistoryEntries {
    List<? extends HistoryEntry> findAllOfUserBetween(OffsetDateTime start, OffsetDateTime end, Long personId);
}
