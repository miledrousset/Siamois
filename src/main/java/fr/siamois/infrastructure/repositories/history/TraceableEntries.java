package fr.siamois.infrastructure.repositories.history;

import fr.siamois.models.TraceableEntity;

import java.time.OffsetDateTime;
import java.util.List;

public interface TraceableEntries {
    List<? extends TraceableEntity> findAllCreatedBetweenByUser(OffsetDateTime start, OffsetDateTime end, Long personId);
}
