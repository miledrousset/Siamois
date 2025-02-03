package fr.siamois.infrastructure.repositories.history;

import fr.siamois.models.TraceInfo;
import fr.siamois.models.TraceableEntity;
import fr.siamois.models.history.GlobalHistoryEntry;

import java.time.OffsetDateTime;
import java.util.List;

public interface GlobalHistoryRepository {
    List<GlobalHistoryEntry> findAllHistoryOfUserBetween(String tableName, TraceInfo traceInfo, OffsetDateTime start, OffsetDateTime end);
    List<TraceableEntity> findAllCreationOfUserBetween(String tableName, TraceInfo traceInfo, OffsetDateTime start, OffsetDateTime end);
}
