package fr.siamois.models.history;

import fr.siamois.models.actionunit.ActionUnit;
import fr.siamois.models.actionunit.ActionUnitParent;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.time.OffsetDateTime;

@EqualsAndHashCode(callSuper = true)
@Getter
@Entity
@Table(name = "history_action_unit")
@Immutable
public class ActionUnitHist extends ActionUnitParent implements HistoryEntry<ActionUnit> {

    @Id
    @Column(name = "history_id")
    private Long id;

    @Column(name = "action_unit_id", nullable = false)
    private Long tableId;

    @Column(name = "update_type", length = 10)
    @Enumerated(EnumType.STRING)
    private HistoryUpdateType updateType;

    @Column(name = "update_time")
    private OffsetDateTime updateTime;

}
