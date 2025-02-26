package fr.siamois.domain.models.history;

import java.time.OffsetDateTime;

public record HistoryOperation(HistoryUpdateType type, String entityName, Long entityNumber, OffsetDateTime actionDatetime) { }
