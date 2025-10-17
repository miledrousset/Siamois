package fr.siamois.domain.models.history;

import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.actionunit.ActionUnitParent;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.time.OffsetDateTime;

@Deprecated
@EqualsAndHashCode(callSuper = true)
@Getter
@Entity
@Table(name = "history_action_unit")
@Immutable
public class ActionUnitHist extends ActionUnitParent implements HistoryEntry<ActionUnit> {

    @Id
    @Column(name = "history_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "action_unit_id", nullable = false)
    private Long tableId;

    @Column(name = "update_type", length = 10)
    @Enumerated(EnumType.STRING)
    private HistoryUpdateType updateType;

    @Column(name = "update_time")
    private OffsetDateTime updateTime;

}
