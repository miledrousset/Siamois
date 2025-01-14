package fr.siamois.models.history;

import fr.siamois.models.SpatialUnitParent;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

@EqualsAndHashCode(callSuper = true)
@Getter
@Entity
@Table(name = "history_spatial_unit")
@Immutable
public class SpatialUnitHist extends SpatialUnitParent {

    @Id
    @Column(name = "history_id")
    private Long id;

    @Column(name = "spatial_unit_id", nullable = false)
    private Long tableId;

    @Column(name = "update_type", length = 10)
    @Enumerated(EnumType.STRING)
    private HistoryUpdateType updateType;

}
