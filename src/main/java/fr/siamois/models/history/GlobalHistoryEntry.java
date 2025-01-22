package fr.siamois.models.history;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
public class GlobalHistoryEntry {
    private HistoryUpdateType updateType;
    private Long tableId;
    private OffsetDateTime updateTime;
}
