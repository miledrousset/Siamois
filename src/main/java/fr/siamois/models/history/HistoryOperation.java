package fr.siamois.models.history;

import java.time.OffsetDateTime;

public record HistoryOperation(HistoryUpdateType type, String entityName, Long entityNumber, OffsetDateTime actionDatetime) { }
