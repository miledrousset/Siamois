package fr.siamois.domain.models.history;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;

@Deprecated
@Data
@AllArgsConstructor
public class GlobalHistoryEntry {
    private HistoryUpdateType updateType;
    private Long tableId;
    private OffsetDateTime updateTime;
}
