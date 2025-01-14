package fr.siamois.models.history;

import fr.siamois.models.ActionUnitParent;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

@EqualsAndHashCode(callSuper = true)
@Getter
@Entity
@Table(name = "history_action_unit")
@Immutable
public class ActionUnitHist extends ActionUnitParent {

    @Id
    @Column(name = "history_id")
    private Long id;

    @Column(name = "action_unit_id", nullable = false)
    private Long tableId;

    @Column(name = "update_type", length = 10)
    @Enumerated(EnumType.STRING)
    private HistoryUpdateType updateType;

}
